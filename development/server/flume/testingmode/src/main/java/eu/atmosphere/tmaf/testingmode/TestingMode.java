package eu.atmosphere.tmaf.testingmode;

import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.source.kafka.KafkaSource;

public class TestingMode extends KafkaSource {
    @Override
    public synchronized ChannelProcessor getChannelProcessor()
    {
        return new CustomChannelProcessorProxy(super.getChannelProcessor());
    }
}
