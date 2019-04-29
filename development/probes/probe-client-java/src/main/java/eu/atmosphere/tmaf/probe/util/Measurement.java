/**
 * <b>ATMOSPHERE</b> - http://www.atmosphere-eubrazil.eu/
 * ***
 * <p>
 * <b>Trustworthiness Monitoring & Assessment Framework</b>
 * Component: TMA_Monitor and Probes
 * <p>
 * Repository: https://github.com/eubr-atmosphere/tma-framework
 * License: https://github.com/eubr-atmosphere/tma-framework/blob/master/LICENSE
 * <p>
 * <p>
 */
package eu.atmosphere.tmaf.probe.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
public class Measurement {

    private final AtomicLong measurementIdSerial = new AtomicLong(0);
    private final Map<Long, Long> ongoigMeasurements = new ConcurrentHashMap<>();

    /**
     * Regists the start of one measurement.
     *
     * @return the id of the measurement.
     */
    public long begin() {
        long id = measurementIdSerial.incrementAndGet();
        ongoigMeasurements.put(id, System.nanoTime());
        return id;
    }

    /**
     * Returns a partial measurement of time in the default unit (Millis).
     *
     * @param id the id of the measurement, obtained in {@link #begin()}.
     * @return the duration of the partial in MILLISECONDS.
     *         {@code -1} in case of error.
     */
    public long partial(long id) {
        return partial(id, TimeUnit.MILLISECONDS);
    }

    /**
     * Returns a final measurement of time in the default unit (Millis).
     *
     * @param id the id of the measurement, obtained in {@link #begin()}.
     * @return the duration of the partial in MILLISECONDS.
     *         {@code -1} in case of error.
     */
    public long end(long id) {
        return end(id, TimeUnit.MILLISECONDS);
    }

    /**
     * Returns a partial measurement of time.
     * <br/>
     * Id is not deleted, can be called again in the future.
     *
     * @param id   the id of the measurement, obtained in {@link #begin()}.
     * @param unit the unit of time in which the result should be presented.
     * @return the duration of the partial in {@code TimeUnit unit}.
     *         {@code -1} in case of error.
     */
    public long partial(long id, TimeUnit unit) {
        long part = System.nanoTime();
        Long begin = ongoigMeasurements.get(id);
        if (begin == null) {
            return -1;
        }
        return unit.convert(part - begin, TimeUnit.NANOSECONDS);
    }

    /**
     * Returns the final measurement of time.
     * <br/>
     * Id is deleted, and therefore cannot be called again in the future.
     *
     * @param id   the id of the measurement, obtained in {@link #begin()}.
     * @param unit the unit of time in which the result should be presented.
     * @return the duration of the partial in {@code TimeUnit unit}.
     *         {@code -1} in case of error.
     */
    public long end(long id, TimeUnit unit) {
        long end = System.nanoTime();
        Long begin = ongoigMeasurements.remove(id);
        if (begin == null) {
            return -1;
        }
        return unit.convert(end - begin, TimeUnit.NANOSECONDS);
    }
}
