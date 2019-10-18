/**
 * <b>ATMOSPHERE</b> - http://www.atmosphere-eubrazil.eu/
 * ***
 * <p>
 * <b>Trustworthiness Monitoring & Assessment Framework</b>
 * Component: Monitor - Probe
 * <p>
 * Repository: https://github.com/eubr-atmosphere/tma-framework
 * License: https://github.com/eubr-atmosphere/tma-framework/blob/master/LICENSE
 * <p>
 * <p>
 */
package eu.atmosphere.tmaf.probe;

import eu.atmosphere.tmaf.monitor.client.BackgroundClient;
import eu.atmosphere.tmaf.monitor.message.Data;
import eu.atmosphere.tmaf.monitor.message.Message;
import eu.atmosphere.tmaf.monitor.message.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import javax.management.openmbean.CompositeData;
import javax.management.ObjectName;
import java.time.Instant;
import eu.atmosphere.tmaf.probe.utils.PropertiesManager;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

/**
 * Probe that collects Memory nad CPU from Java Application Servers.
 * <p>
 *
 * @author Rui Silva <rfsilva@student.dei.uc.pt>
 */
public class TMAProbe {

    private static final Logger LOGGER = LoggerFactory.getLogger(TMAProbe.class);

    private static final String ENDPOINT
            = PropertiesManager.getInstance().getProperty("JMX_ENDPOINT");
    private static final String DESCRIPTION_ID_CPU
            = PropertiesManager.getInstance().getProperty("DESCRIPTION_ID_CPU");
    private static final String DESCRIPTION_ID_MEMORY
            = PropertiesManager.getInstance().getProperty("DESCRIPTION_ID_MEMORY");
    private static final String RESOURCE_ID
            = PropertiesManager.getInstance().getProperty("RESOURCE_ID");
    private static final String PASSWORD
            = PropertiesManager.getInstance().getProperty("PASSWORD");

    private static final int PROBE_ID
            = PropertiesManager.getInstance().getIntegerProperty("PROBE_ID");

    private static final int DEFAULT_PROBE_DELAY = 1000;

    private static final AtomicBoolean running = new AtomicBoolean(false);

    public enum Result {
        PROBE_ALREADY_RUNNING,
        INVALID_JMX_SERVICE_URL,
        SUCCESS
    }

    public static Result startReporting() {

        if (running.get()) {
            return Result.PROBE_ALREADY_RUNNING;
        }

        running.set(true);

        BackgroundClient client = new BackgroundClient();

        client.authenticate(PROBE_ID, PASSWORD.getBytes());

        int probingDelay = PropertiesManager.getInstance().getIntegerProperty("PROBING_DELAY");
        if (PropertiesManager.INVALID_INTEGER_ERROR_CODE == probingDelay) {
            LOGGER.warn("Invalid probing delay {}!! Defaulting to!", probingDelay, DEFAULT_PROBE_DELAY);
            probingDelay = DEFAULT_PROBE_DELAY;
        }

        final MBeanServerConnection server;
        try {
            JMXServiceURL url = new JMXServiceURL(ENDPOINT);
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            jmxc.connect();
            server = jmxc.getMBeanServerConnection();
        } catch (IOException ex) {
            LOGGER.error("Invalid JMXServiceURL or Connection!", ex);
            return Result.INVALID_JMX_SERVICE_URL;
        }

        boolean start = client.start();
        LOGGER.trace("BackgroundClient started: {}!", start);
        running.set(true);

        /**
         * For the inner thread;
         */
        final int probingDelayFinal = probingDelay;
        Runnable r = () -> {
            Message message;

            // create object instances that will be used to get memory and operating 
            // system Mbean objects exposed by JMX; create variables for cpu time 
            // and system time before
            Object memoryMbean;
            Object osMbean;
            long cpuBefore = 0;
            CompositeData cd;

            while (running.get()) {

                try {
                    //get an instance of the HeapMemoryUsage Mbean
                    memoryMbean = server.getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
                    cd = (CompositeData) memoryMbean;
                    //get an instance of the OperatingSystem Mbean
                    osMbean = server.getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "ProcessCpuTime");

                    //get system time and cpu time from last poll
                    long cpuAfter = Long.parseLong(osMbean.toString());
                    //find cpu time between our first and last jmx poll
                    long cpuDiff = cpuAfter - cpuBefore;
                    cpuBefore = cpuAfter;

                    message = client.createMessage();
                    message.setResourceId(Integer.parseInt(RESOURCE_ID));
                    message.addData(new Data(Data.Type.MEASUREMENT,
                            Integer.parseInt(DESCRIPTION_ID_MEMORY),
                            new Observation(Instant.now().getEpochSecond(), Double.parseDouble(cd.get("used").toString()))));
                    message.addData(new Data(Data.Type.MEASUREMENT,
                            Integer.parseInt(DESCRIPTION_ID_CPU),
                            new Observation(Instant.now().getEpochSecond(), (double) cpuDiff)));

                    client.send(message);

                } catch (IOException | MalformedObjectNameException | MBeanException
                        | AttributeNotFoundException | InstanceNotFoundException
                        | ReflectionException | NumberFormatException ex) {
                    LOGGER.warn("Unable to read metrics, no message created or sent.", ex);
                }

                try {
                    Thread.sleep(probingDelayFinal);
                } catch (InterruptedException ex) {
                    LOGGER.trace("Sleep Interrupted.", ex);
                }
            }
        };

        Executors.newSingleThreadExecutor().submit(r);

        return Result.SUCCESS;
    }

    public static void stopReporting() {
        running.set(false);
    }
}
