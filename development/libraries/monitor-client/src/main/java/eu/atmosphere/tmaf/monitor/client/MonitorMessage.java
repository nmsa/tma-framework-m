/**
 * <b>ATMOSPHERE</b> - http://www.atmosphere-eubrazil.eu/
 * ***
 * <p>
 * <b>Trustworthiness Monitoring & Assessment Framework</b>
 * Component: Monitor Client
 * <p>
 * Repository: https://github.com/nmsa/tma-framework
 * License: https://github.com/nmsa/tma-framework/blob/master/LICENSE
 * <p>
 * <p>
 */
package eu.atmosphere.tmaf.monitor.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * tma-m_schema_0_3
 * <p>
 * <p>
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
public class MonitorMessage {

    /**
     *
     * (Required)
     * <p>
     */
    private Integer probeId = -1;
    /**
     *
     * (Required)
     * <p>
     */
    private Integer resourceId = -1;
    /**
     *
     * (Required)
     * <p>
     */
    private Integer messageId = -1;
    /**
     *
     * (Required)
     * <p>
     */
    private Integer sentTime = -1;
    /**
     *
     * (Required)
     * <p>
     */
    private List<Data> data = new LinkedList<>();

    MonitorMessage() {
    }

    public Integer getProbeId() {
        return probeId;
    }

    public void setProbeId(Integer probeId) {
        this.probeId = probeId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getMessageId() {
        return messageId;
    }

//    public void setMessageId(Integer messageId) {
//        this.messageId = messageId;
//    }
    public Integer getSentTime() {
        return sentTime;
    }

    public void setSentTime(Integer sentTime) {
        this.sentTime = sentTime;
    }

    public List<Data> getData() {
        return data;
    }

    public boolean addData(Data datum) {
        return this.data.add(datum);
    }
//    public void setData(List<Data> data) {
//        this.data = data;
//    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorMessage{").append("probeId:").append(probeId).
                append("resourceId:").append(resourceId).append("messageId:").
                append(messageId).append("sentTime:").append(sentTime).
                append("data:").append(data).append("}").toString();
    }
}
