/**
 * <b>ATMOSPHERE</b> - http://www.atmosphere-eubrazil.eu/
 * ***
 * <p>
 * <b>Trustworthiness Monitoring & Assessment Framework</b>
 * Component: // FIXME: Define associated component
 * <p>
 * Repository: https://github.com/nmsa/tma-framework
 * License: https://github.com/nmsa/tma-framework/blob/master/LICENSE
 * <p>
 * <p>
 */
package eu.atmosphere.tmaf.probe;

import eu.atmosphere.tmaf.monitor.client.MonitorClient;
import eu.atmosphere.tmaf.monitor.client.MonitorMessage;
import eu.atmosphere.tmaf.monitor.message.MonitorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static void main(String[] args) {
        LOGGER.info("Trust me! This is ATMOSPHERE!");
        MonitorClient client = new MonitorClient();
        
        MonitorMessage message = client.createMessage();
        
//        message.setMessageId(Integer.SIZE);
        
        
        int result = client.send(message);
        

        LOGGER.info("Trust me! This is ATMOSPHERE!");
    }
}
