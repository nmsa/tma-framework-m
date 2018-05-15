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
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
class Data {

    /**
     *
     * (Required)
     * <p>
     */
    private Data.Type type = Data.Type.fromValue("measurement");
    /**
     *
     * (Required)
     * <p>
     */
    private Integer descriptionId = -10;
    /**
     *
     * (Required)
     * <p>
     */
    private List<Observation> observations = null;

    /**
     *
     * (Required)
     * <p>
     */
    public Data.Type getType() {
        return type;
    }

    /**
     *
     * (Required)
     * <p>
     */
    public void setType(Data.Type type) {
        this.type = type;
    }

    /**
     *
     * (Required)
     * <p>
     */
    public Integer getDescriptionId() {
        return descriptionId;
    }

    /**
     *
     * (Required)
     * <p>
     */
    public void setDescriptionId(Integer descriptionId) {
        this.descriptionId = descriptionId;
    }

    /**
     *
     * (Required)
     * <p>
     */
    public List<Observation> getObservations() {
        return observations;
    }

    /**
     *
     * (Required)
     * <p>
     */
    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    @Override
    public String toString() {
        return new StringBuilder("Datum{").append("type:").append(type).
                append("descriptionId:").append(descriptionId).
                append("observations:").append(observations).append("}").
                toString();
    }

    public enum Type {

        MEASUREMENT("measurement"),
        EVENT("event");
        private final String value;
        private final static Map<String, Data.Type> CONSTANTS
                = new HashMap<String, Data.Type>();

        static {
            for (Data.Type c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static Data.Type fromValue(String value) {
            Data.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }
}
