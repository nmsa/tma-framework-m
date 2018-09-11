package eu.atmosphere.tmaf.testingmode;


import org.apache.flume.ChannelSelector;
import org.apache.flume.Event;
import org.apache.flume.channel.ChannelProcessor;

import org.apache.flume.event.EventBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public byte[] generateEvent (int probeId, int descriptionId, int resourceId, String time, double value) {
        StringBuilder str = new StringBuilder();
        str.append("INSERT INTO Data(probeId,descriptionId,resourceId,valueTime,value) VALUES (");
        str.append(probeId);
        str.append(",");
        str.append(descriptionId);
        str.append(",");
        str.append(resourceId);
        str.append(",");
        str.append("\'");
        str.append(time);
        str.append("\'");
        str.append(",");
        str.append(value);
        str.append(")");
        String message = str.toString();
        byte[] payload = message.getBytes();
        return payload;
    }
    public List<Event> parseEvents (List<Event> evts){
        List<Event> listEvents = new ArrayList<Event>();
        for (Event event : evts){
            byte[] eventBody = event.getBody();
            JSONObject input = new JSONObject(new String(eventBody));
            int probeId = input.getInt("probeId");
            int resourceId = input.getInt("resourceId");
            JSONArray data = input.getJSONArray("data");
            for (int i=0; i<data.length();i++) {
                JSONArray observations = data.getJSONObject(i).getJSONArray("observations");
                String type = data.getJSONObject(i).getString("type");
                for (int j = 0; j < observations.length(); j++) {
                    int descriptionId = data.getJSONObject(i).getInt("descriptionId");
                    int time = observations.getJSONObject(j).getInt("time");
                    Date miliseconds= new Date(time*1000L);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String timestamp = dateFormat.format(miliseconds);
                    Double value = observations.getJSONObject(j).getDouble("value");
                    byte[] payload = generateEvent(probeId, descriptionId, resourceId, timestamp, value);
                    Event observationEvent = EventBuilder.withBody(payload);
                    listEvents.add(observationEvent);
                }
            }
        }
        return listEvents;
    }

    @Override
    public void processEventBatch(List<Event> events) {
        List<Event> generatedEvents = parseEvents(events);
        m_downstreamChannelProcessor.processEventBatch(generatedEvents);
    }
}
