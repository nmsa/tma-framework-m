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
package eubr.atmosphere.tma.probes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.apache.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import eu.atmosphere.tmaf.monitor.client.BackgroundClient;
import eu.atmosphere.tmaf.monitor.message.Data;
import eu.atmosphere.tmaf.monitor.message.Message;
import eu.atmosphere.tmaf.monitor.message.Observation;

/**
 * Probe that collects the data from kubernetes pods
 * <p>
 *
 * @author Jose DAbruzzo Pereira <josep@dei.uc.pt>
 */
public class ProbeKubernetes {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProbeKubernetes.class);

    private static final int cpuDescriptionId = 1;
    private static final int memoryDescriptionId = 2;

    private static final int probeId = 1098;

    private static final String endpoint = "https://10.100.166.233:5000/monitor";

    private static Map<String, Integer> resourceKeyMap = new HashMap<String, Integer>();

    public static void main(String[] args) {

        LOGGER.info("Trust me! This is ATMOSPHERE!");
        BackgroundClient client = new BackgroundClient(endpoint);

        client.authenticate(probeId, "pass".getBytes());

        boolean start = client.start();
        LOGGER.info("start {}!", start);

        // TODO Check if it is not possible to select all the pods from one namespace

        /*String uri = "http://127.0.0.1:8001/apis/metrics.k8s.io/v1beta1/namespace/"
                + namespaceName + + "/pods/" + podName;
        HttpResponse response = requestRestService(uri);
        InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());*/

        String path = "/atmosphere/tma/probe/probe-kubernetes-api/src/main/resources/data.json";
        InputStream input;
        try {
            input = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(input);
            parsePodMetrics(isr, client);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

        client.shutdown();
        LOGGER.info("Trust me! This is ATMOSPHERE!");
    }

    private static int getResourceId(String podName) {
        if (resourceKeyMap.containsKey(podName)) {
            return resourceKeyMap.get(podName);
        } else {
            Random random = new Random();
            int podKey = random.nextInt(1000) + 1;
            resourceKeyMap.put(podName, podKey);
            return podKey;
        }
    }

    private static void parsePodMetrics(InputStreamReader isr, BackgroundClient client) {

        Gson gson = new GsonBuilder().create();
        Object rawJson = gson.fromJson(isr, Object.class);
        LinkedTreeMap<String, Object> c = (LinkedTreeMap<String, Object>) rawJson;
        List<Object> items = (List<Object>) c.get("items");
        int messageId = 0;

        for (Object object : items) {
            LinkedTreeMap<String, Object> podData = (LinkedTreeMap<String, Object>) object;
            Object metadata = podData.get("metadata");
            LinkedTreeMap<String, Object> ltmMetadata = (LinkedTreeMap<String, Object>) metadata;
            String podName = ltmMetadata.get("name").toString();

            Message message = client.createMessage();

            int resourceId = getResourceId(podName);
            message.setResourceId(resourceId);

            List<Object> containers = (List<Object>) podData.get("containers");
            for (Object container: containers) {

                LinkedTreeMap<String, Object> ltmContainers = (LinkedTreeMap<String, Object>) container;
                LinkedTreeMap<String, Object> ltmUsage = (LinkedTreeMap<String, Object>) ltmContainers.get("usage");

                String cpuString = ltmUsage.get("cpu").toString();
                int cpu = Integer.parseInt(cpuString.substring(0, cpuString.length() - 1));
                Data cpuDatum = new Data(Data.Type.MEASUREMENT, cpuDescriptionId,
                        new Observation((new Date()).getTime(), cpu));
                message.addData(cpuDatum);

                String memoryString = ltmUsage.get("memory").toString();
                int memory = Integer.parseInt(memoryString.substring(0, memoryString.length() - 2));
                Data memoryDatum = new Data(Data.Type.MEASUREMENT, memoryDescriptionId,
                        new Observation((new Date()).getTime(), memory));
                message.addData(memoryDatum);
            }
            message.setSentTime((new Date()).getTime());
            message.setMessageId(messageId++);
            System.out.println(message);
            client.send(message);
        }
    }

}
