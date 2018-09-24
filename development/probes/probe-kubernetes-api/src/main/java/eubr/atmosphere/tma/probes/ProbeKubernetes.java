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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final int cpuDescriptionId = 27;
    private static final int memoryDescriptionId = 28;

    private static final int probeId = 7;

    private static final String endpoint = "https://10.100.166.233:5000/monitor";
    private static final String metricsEndpoint =
            "http://192.168.122.34:8089/apis/metrics.k8s.io/v1beta1/";

    private static final String namespaceName = "default";

    private static Map<String, Integer> resourceKeyMap = new HashMap<String, Integer>();

    private static int messageId = 0;

    private static int minutes = 240;

    public static void main(String[] args) {

        LOGGER.info("Trust me! This is ATMOSPHERE!");
        BackgroundClient client = new BackgroundClient(endpoint);

        if (args.length > 0)
            minutes = Integer.parseInt(args[0]);

        client.authenticate(probeId, "pass".getBytes());

        boolean start = client.start();
        LOGGER.info("start {}!", start);

        // TODO: Remove it! It is just sample data
        resourceKeyMap.put("kafka-0", 8);
        resourceKeyMap.put("wildfly-0", 9);
        resourceKeyMap.put("mysql-wsvd-0", 10);

        resourceKeyMap.put("virtmanagernode-standard-pc-i440fx-piix-1996", 11);
        resourceKeyMap.put("virtmanagermaster-standard-pc-i440fx-piix-1996", 12);

        String uriPods = metricsEndpoint + "namespaces/" + namespaceName + "/pods/";
        String uriNodes = metricsEndpoint + "nodes/";
        HttpResponse response;
        InputStreamReader isr;
        try {
            for (int i = 0; i < minutes; i++) {
                response = requestRestService(uriPods);
                isr = new InputStreamReader(response.getEntity().getContent());
                parsePodMetrics(isr, client);

                response = requestRestService(uriNodes);
                isr = new InputStreamReader(response.getEntity().getContent());
                parseNodeMetrics(isr, client);
                Thread.sleep(60000);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            Thread.sleep(20000);
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

    private static HttpResponse requestRestService(String uri) throws ClientProtocolException, IOException {
      HttpClient client = new DefaultHttpClient();
      HttpGet request = new HttpGet(uri);
      HttpResponse response = client.execute(request);

      return response;
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

        for (Object object : items) {
            LinkedTreeMap<String, Object> podData = (LinkedTreeMap<String, Object>) object;
            Object metadata = podData.get("metadata");
            LinkedTreeMap<String, Object> ltmMetadata = (LinkedTreeMap<String, Object>) metadata;
            String podName = ltmMetadata.get("name").toString();

            if (isMonitorizedPod(podName)) {
                Message message = client.createMessage();
                int resourceId = getResourceId(podName);
                message.setResourceId(resourceId);

                List<Object> containers = (List<Object>) podData.get("containers");
                for (Object container: containers) {

                    LinkedTreeMap<String, Object> ltmContainers = (LinkedTreeMap<String, Object>) container;
                    LinkedTreeMap<String, Object> ltmUsage = (LinkedTreeMap<String, Object>) ltmContainers.get("usage");

                    String cpuString = ltmUsage.get("cpu").toString();
                    message.addData(parseDatumValue(cpuString, cpuDescriptionId, 1));

                    String memoryString = ltmUsage.get("memory").toString();
                    message.addData(parseDatumValue(memoryString, memoryDescriptionId, 2));

                    System.out.println(cpuString);
                    System.out.println(memoryString);
                }
                message.setSentTime((new Date()).getTime());
                message.setMessageId(messageId++);
                System.out.println(message);
                client.send(message);
            }
        }
    }

    private static void parseNodeMetrics(InputStreamReader isr, BackgroundClient client) {

        Gson gson = new GsonBuilder().create();
        Object rawJson = gson.fromJson(isr, Object.class);
        LinkedTreeMap<String, Object> c = (LinkedTreeMap<String, Object>) rawJson;
        List<Object> items = (List<Object>) c.get("items");

        for (Object object : items) {
            LinkedTreeMap<String, Object> nodeData = (LinkedTreeMap<String, Object>) object;
            Object metadata = nodeData.get("metadata");
            LinkedTreeMap<String, Object> ltmMetadata = (LinkedTreeMap<String, Object>) metadata;
            String nodeName = ltmMetadata.get("name").toString();

            if (isMonitorizedNode(nodeName)) {
                LinkedTreeMap<String, Object> ltmUsage = (LinkedTreeMap<String, Object>) nodeData.get("usage");

                Message message = client.createMessage();
                int resourceId = getResourceId(nodeName);
                message.setResourceId(resourceId);

                String cpuString = ltmUsage.get("cpu").toString();
                message.addData(parseDatumValue(cpuString, cpuDescriptionId, 1));

                String memoryString = ltmUsage.get("memory").toString();
                message.addData(parseDatumValue(memoryString, memoryDescriptionId, 2));

                message.setSentTime(Calendar.getInstance().getTime().getTime());
                message.setMessageId(messageId++);

                System.out.println(message);
                client.send(message);
            }
        }
    }

    private static Data parseDatumValue(String valueString, int descriptionId, int unitLength) {
        int value = 0;
        if (!"0".equals(valueString.trim())) {
            value = Integer.parseInt(valueString.substring(0, valueString.length() - unitLength));
        }
        Data datum = new Data(Data.Type.MEASUREMENT, descriptionId,
                new Observation(Calendar.getInstance().getTime().getTime(), value));
        return datum;
    }

    private static boolean isMonitorizedPod(String podName) {
        return podName.startsWith("wildfly-") || podName.startsWith("mysql-wsvd-") || podName.startsWith("kafka-");
    }

    private static boolean isMonitorizedNode(String nodeName) {
        return nodeName.startsWith("virtmanagernode-");
    }

}