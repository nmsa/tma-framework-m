package eubr.atmosphere.tma.probes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

public class ProbeKubernetes {

	public static void main(String[] args) {
		// TODO Check if it is not possible to select all the pods from one namespace
		
		/*String uri = "http://127.0.0.1:8001/apis/metrics.k8s.io/v1beta1/namespace/"
				+ namespaceName + + "/pods/" + podName;
		HttpResponse response = requestRestService(uri);
		InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());*/
		
		String path = "/Users/josealexandredabruzzopereira/Projects/tma-framework-m/development/probes/probes-kubernetes-api/src/main/java/eubr/atmosphere/tma/probes/data.json";
		InputStream input;
		try {
			input = new FileInputStream(path);
			InputStreamReader isr = new InputStreamReader(input);
			parsePodMetrics(isr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void parsePodMetrics(InputStreamReader isr) {
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
				System.out.println(ltmUsage.get("cpu"));
				System.out.println(ltmUsage.get("memory"));
			}
			// metadata.name : this is to be used to identify the resourceId
			// containers.usage.cpu
			// containers.usage.memory
		}
	}

}
