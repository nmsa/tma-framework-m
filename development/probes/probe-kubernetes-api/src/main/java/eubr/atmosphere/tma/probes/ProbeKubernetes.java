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
import java.util.List;
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

    public static void main(String[] args) {

        LOGGER.info("Trust me! This is ATMOSPHERE!");
        BackgroundClient client = new BackgroundClient();

        client.authenticate(1098, "pass".getBytes());

        boolean start = client.start();
        LOGGER.info("start {}!", start);

        // TODO Check if it is not possible to select all the pods from one namespace

        /*String uri = "http://127.0.0.1:8001/apis/metrics.k8s.io/v1beta1/namespace/"
                + namespaceName + + "/pods/" + podName;
        HttpResponse response = requestRestService(uri);
        InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());*/

        String path = "/home/virt-atm/Documents/tma-framework-m/development/probes/probe-kubernetes-api/src/main/resources/data.json";
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

    private static void parsePodMetrics(InputStreamReader isr, BackgroundClient client) {

        Message message;
        message = client.createMessage();

        // TODO: to define the resource ID!!!!
        message.setResourceId(101098);

        Gson gson = new GsonBuilder().create();
        Object rawJson = gson.fromJson(isr, Object.class);
        LinkedTreeMap<String, Object> c = (LinkedTreeMap<String, Object>) rawJson;
        List<Object> items = (List<Object>) c.get("items");
        for (Object object : items) {
            LinkedTreeMap<String, Object> podData = (LinkedTreeMap<String, Object>) object;
            System.out.println(podData);
            Object metadata = podData.get("metadata");
            LinkedTreeMap<String, Object> ltmMetadata = (LinkedTreeMap<String, Object>) metadata;
            System.out.println(ltmMetadata.get("name"));
            List<Object> containers = (List<Object>) podData.get("containers");
            for (Object container: containers) {

                LinkedTreeMap<String, Object> ltmContainers = (LinkedTreeMap<String, Object>) container;
                LinkedTreeMap<String, Object> ltmUsage = (LinkedTreeMap<String, Object>) ltmContainers.get("usage");

                String cpuString = ltmUsage.get("cpu").toString();
                int cpu = Integer.parseInt(cpuString.substring(0, cpuString.length() - 1));
                System.out.println(cpu);
                int descriptionId = 1;
                Data datum = new Data(Data.Type.MEASUREMENT, descriptionId,
                        new Observation((new Date()).getTime(), cpu));
                System.out.println(datum);
                message.addData(datum);

                System.out.println(ltmUsage.get("cpu"));
                System.out.println(ltmUsage.get("memory"));
            }
            // metadata.name : this is to be used to identify the resourceId
            // containers.usage.cpu
            // containers.usage.memory
        }
        client.send(message);
    }

}
