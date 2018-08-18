package flume.mycustomsource;

import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.source.kafka.KafkaSource;

public class CustomSource extends KafkaSource {
    @Override
    public synchronized ChannelProcessor getChannelProcessor()
    {
        return new CustomChannelProcessorProxy(super.getChannelProcessor());
    }

}


