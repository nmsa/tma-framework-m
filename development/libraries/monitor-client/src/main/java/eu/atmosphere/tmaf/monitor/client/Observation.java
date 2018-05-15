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
import java.util.Map;

/**
 *
 * FIXME
 *
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
class Observation {

    /**
     *
     * (Required)
     * <p>
     */
    private Integer time = -1;
    private Double value = 0.0D;

    /**
     *
     * (Required)
     * <p>
     */
    public Integer getTime() {
        return time;
    }

    /**
     *
     * (Required)
     * <p>
     */
    public void setTime(Integer time) {
        this.time = time;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringBuilder("{").append("time:").append(time).
                append("value:").append(value).append("}").toString();
    }

}
