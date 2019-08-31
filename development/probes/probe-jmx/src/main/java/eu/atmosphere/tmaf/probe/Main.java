/**
 * <b>ATMOSPHERE</b> - http://www.atmosphere-eubrazil.eu/
 * ***
 * <p>
 * <b>Trustworthiness Monitoring & Assessment Framework</b>
 * Component: // FIXME: Define associated component
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
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import javax.management.openmbean.CompositeData;
import javax.management.ObjectName;
import java.net.MalformedURLException;
import java.time.Instant;
import eu.atmosphere.tmaf.probe.utils.PropertiesManager;


/**
 * Please, add a short description of the class.
 * <p>
 *
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String endpoint = PropertiesManager.getInstance().getProperty("JMX_ENDPOINT");

    private static int messageId = 0;

    /**P
     * Probe that collects Memory nad CPU from Java Application Servers
     *
     * @param args
     */
    public static void main(String[] args) throws Exception{
        LOGGER.info("Trust me! This is ATMOSPHERE!");
//        BackgroundClient client = new BackgroundClient("http://127.0.0.1:5000/monitor");
       BackgroundClient client = new BackgroundClient();

        client.authenticate(1098, "pass".getBytes());

        Message message;

        boolean start = client.start();
        LOGGER.info("start {}!", start);

        while (true)
        {
           message = client.createMessage();
           message.setResourceId(30018);

           JMXServiceURL url = new JMXServiceURL(endpoint);

           JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
           jmxc.connect();

           //create object instances that will be used to get memory and operating system Mbean objects exposed by JMX; create variables for cpu time and system time before
           Object memoryMbean = null;
           Object osMbean = null;
           long cpuBefore = 0;
           CompositeData cd = null;

           // call the garbage collector before the test using the Memory Mbean
           jmxc.getMBeanServerConnection().invoke(new ObjectName("java.lang:type=Memory"), "gc", null, null);



           //get an instance of the HeapMemoryUsage Mbean
           memoryMbean = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
           cd = (CompositeData) memoryMbean;
           //get an instance of the OperatingSystem Mbean
           osMbean = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=OperatingSystem"),"ProcessCpuTime");


           //get system time and cpu time from last poll
           long cpuAfter = Long.parseLong(osMbean.toString());


           long cpuDiff = cpuAfter - cpuBefore; //find cpu time between our first and last jmx poll
           cpuBefore = cpuAfter;

           message.addData(new Data(Data.Type.MEASUREMENT, 30082, new Observation(Instant.now().getEpochSecond(), Double.parseDouble(cd.get("used").toString()))));
           message.addData(new Data(Data.Type.MEASUREMENT, 30083, new Observation(Instant.now().getEpochSecond(), (double)cpuDiff)));

           client.send(message);
           messageId++;
           Thread.sleep(1000); //delay for one second
    }
  }
}
