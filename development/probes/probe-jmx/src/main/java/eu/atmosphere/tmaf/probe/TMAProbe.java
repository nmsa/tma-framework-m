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
import java.util.concurrent.atomic.AtomicBoolean;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
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

    private static final String ENDPOINT = PropertiesManager.getInstance().getProperty("JMX_ENDPOINT");
    private static final String DESCRIPTION_ID_CPU = PropertiesManager.getInstance().getProperty("DESCRIPTION_ID_CPU");
    private static final String DESCRIPTION_ID_MEMORY = PropertiesManager.getInstance().getProperty("DESCRIPTION_ID_MEMORY");
    private static final String RESOURCE_ID = PropertiesManager.getInstance().getProperty("RESOURCE_ID");

    private static final int DEFAULT_PROBE_DELAY = 1000;

    private static final AtomicBoolean running = new AtomicBoolean(false);

    public static boolean startReporting() {
        BackgroundClient client = new BackgroundClient();

        // TODO: this passwords should come from...
        client.authenticate(1098, "pass".getBytes());

        int probingDelay = PropertiesManager.getInstance().getIntegerProperty("PROBING_DELAY");
        if (PropertiesManager.INVALID_INTEGER_ERROR_CODE == probingDelay) {
            LOGGER.warn("Invalid probing delay {}!! Defaulting to!", probingDelay, DEFAULT_PROBE_DELAY);
            probingDelay = DEFAULT_PROBE_DELAY;
        }

        JMXConnector jmxc = null;
        try {
            JMXServiceURL url = new JMXServiceURL(ENDPOINT);
            jmxc = JMXConnectorFactory.connect(url, null);
            jmxc.connect();
        } catch (IOException ex) {
            LOGGER.error("Invalid JMXServiceURL or Connection!", ex);
            return false;
        }

        boolean start = client.start();
        LOGGER.info("start {}!", start);

        /**
         * For the inner thread;
         */
        final int probingDelayFinal = probingDelay;
        final JMXConnector jmxcFinal = jmxc;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Message message;

                //create object instances that will be used to get memory and operating system Mbean objects exposed by JMX; create variables for cpu time and system time before
                Object memoryMbean = null;
                Object osMbean = null;
                long cpuBefore = 0;
                CompositeData cd = null;

                while (running.get()) {

                    // Ill advised
//                    // call the garbage collector before the test using the Memory Mbean
//                    jmxcFinal.getMBeanServerConnection().invoke(new ObjectName("java.lang:type=Memory"), "gc", null, null);
// 
                    try {
                        //get an instance of the HeapMemoryUsage Mbean
                        memoryMbean = jmxcFinal.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
                        cd = (CompositeData) memoryMbean;
                        //get an instance of the OperatingSystem Mbean
                        osMbean = jmxcFinal.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "ProcessCpuTime");

                        //get system time and cpu time from last poll
                        long cpuAfter = Long.parseLong(osMbean.toString());
                        long cpuDiff = cpuAfter - cpuBefore; //find cpu time between our first and last jmx poll
                        cpuBefore = cpuAfter;

                        message = client.createMessage();
                        message.setResourceId(Integer.parseInt(RESOURCE_ID));
                        message.addData(new Data(Data.Type.MEASUREMENT, Integer.parseInt(DESCRIPTION_ID_MEMORY), new Observation(Instant.now().getEpochSecond(), Double.parseDouble(cd.get("used").toString()))));
                        message.addData(new Data(Data.Type.MEASUREMENT, Integer.parseInt(DESCRIPTION_ID_CPU), new Observation(Instant.now().getEpochSecond(), (double) cpuDiff)));

                        client.send(message);

                    } catch (IOException | MalformedObjectNameException | MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException | NumberFormatException ex) {
                        LOGGER.warn("Unable to read metrics, no message created or sent.", ex);

                    }

                    try {
                        Thread.sleep(probingDelayFinal); //delay for configured millis
                    } catch (InterruptedException ex) {
                        LOGGER.trace("Sleep Interrupted.", ex);
                    }
                }
            }
        };

        return true;
    }

    public static void stopReporting() {
        running.set(false);
    }
}
