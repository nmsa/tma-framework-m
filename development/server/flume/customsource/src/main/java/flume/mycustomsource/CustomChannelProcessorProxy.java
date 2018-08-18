package flume.mycustomsource;

import org.apache.flume.ChannelSelector;
import org.apache.flume.Event;
import org.apache.flume.channel.ChannelProcessor;

import org.apache.flume.event.EventBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CustomChannelProcessorProxy extends ChannelProcessor {
    public ChannelProcessor  m_downstreamChannelProcessor = null;

    public CustomChannelProcessorProxy (ChannelSelector selector) {
        super(selector);
    }

    public CustomChannelProcessorProxy (ChannelProcessor processor) {
        super(null);
        m_downstreamChannelProcessor = processor;
    }
    public List<Event> SplitFunction (List<Event> evts){
        List<Event> aux = new ArrayList<Event>();
        for (Event event : evts){
            byte[] enventBody = event.getBody();
            JSONObject testeV = new JSONObject(new String(enventBody));
            JSONArray data = testeV.getJSONArray("data");
            for (int i=0; i<data.length();i++) {
                JSONArray observations = data.getJSONObject(i).getJSONArray("observations");
                for (int j = 0; j < observations.length(); j++) {
                    int probeid = testeV.getInt("probeId");
                    int resourceid = testeV.getInt("resourceId");
                    String type = data.getJSONObject(i).getString("type");
                    int descriptionid = data.getJSONObject(i).getInt("descriptionId");
                    int time = observations.getJSONObject(j).getInt("time");
                    Double value = null;
                    value = observations.getJSONObject(j).getDouble("value");
                    StringBuilder str = new StringBuilder();
                    str.append(probeid);
                    str.append(",");
                    str.append(resourceid);
                    str.append(",");
                    str.append(type);
                    str.append(",");
                    str.append(descriptionid);
                    str.append(",");
                    str.append(time);
                    str.append(",");
                    str.append(value);

                    String message = str.toString();
                    byte[] s = message.getBytes();
                    Event aaa = EventBuilder.withBody(s);
                    aux.add(aaa);
                }
            }
        }
        return aux;
    }

    @Override
    public void processEventBatch(List<Event> events) {
        List<Event> generatedEvents = SplitFunction(events);
        m_downstreamChannelProcessor.processEventBatch(generatedEvents);
    }
}
