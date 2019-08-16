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


/**
 * Please, add a short description of the class.
 * <p>
 *
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Please, document your main methods using <tt>Javadoc</tt>.
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
        int i = 0;

        while (true)
        {
            message = client.createMessage();
            message.setResourceId(101098);

            // create jmx connection with mules jmx agent
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1098/server");

			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			jmxc.connect();

			//create object instances that will be used to get memory and operating system Mbean objects exposed by JMX; create variables for cpu time and system time before
			Object memoryMbean = null;
			Object osMbean = null;
			long cpuBefore = 0;
			long tempMemory = 0;
			CompositeData cd = null;

			// call the garbage collector before the test using the Memory Mbean
			jmxc.getMBeanServerConnection().invoke(new ObjectName("java.lang:type=Memory"), "gc", null, null);



			//get an instance of the HeapMemoryUsage Mbean
			memoryMbean = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
			cd = (CompositeData) memoryMbean;
			//get an instance of the OperatingSystem Mbean
			osMbean = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=OperatingSystem"),"ProcessCpuTime");
			System.out.println("Used memory: " + " " + cd.get("used") + " Used cpu: " + osMbean); //print memory usage
			tempMemory = tempMemory + Long.parseLong(cd.get("used").toString());
			Thread.sleep(1000); //delay for one second


 			//get system time and cpu time from last poll
			long cpuAfter = Long.parseLong(osMbean.toString());


			long cpuDiff = cpuAfter - cpuBefore; //find cpu time between our first and last jmx poll
			cpuBefore = cpuAfter;
			System.out.println("Cpu diff in milli seconds: " + cpuDiff / 1000000); //print cpu time in miliseconds
            

            message.addData(new Data(Data.Type.MEASUREMENT, i, new Observation(Instant.now().getEpochSecond(), Double.parseDouble(cd.get("used").toString())), new Observation(Instant.now().getEpochSecond(), (double)cpuDiff)));

            client.send(message);
            i++;
    }
}
}