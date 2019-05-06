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
//        BackgroundClient client = new BackgroundClient("http://127.0.0.1:5000/monitor");
        BackgroundClient client = new BackgroundClient();

        client.authenticate(1098, "pass".getBytes());

        Message message;

        boolean start = client.start();
        LOGGER.info("start {}!", start);

        for (int i = 0; i < 10; i++) {
            LOGGER.debug("i = " + i);
            message = client.createMessage();
            message.setResourceId(101098);

            message.addData(new Data(Data.Type.EVENT, i, new Observation(100000 + i, 10000.00001 + i)));

            message.addData(new Data(Data.Type.MEASUREMENT, i, new Observation(10000 + i, 10000.00001 + i),
                    new Observation(20000 + i, 20000.00001 + i),
                    new Observation(30000 + i, 30000.00001 + i),
                    new Observation(40000 + i, 40000.00001 + i),
                    new Observation(50000 + i, 50000.00001 + i),
                    new Observation(60000 + i, 60000.00001 + i),
                    new Observation(60000 + i, 60000.00001 + i),
                    new Observation(60000 + i, 60000.00001 + i),
                    new Observation(60000 + i, 60000.00001 + i)
            ));

            client.send(message);
        }

        try {
            Thread.sleep(100000);
            boolean stop = client.stop();
            LOGGER.info("stop {}!", stop);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }

        //        int result = client.send(message);
        client.shutdown();
        LOGGER.info("Trust me! This is ATMOSPHERE!");
    }
}
