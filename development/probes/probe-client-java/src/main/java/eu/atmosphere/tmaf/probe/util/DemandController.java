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

import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
public class DemandController extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(DemandController.class);

    private static final int DEFAULT_BATCH_SLOTS[][] = {
        {1, 2 * 60, 50},
        {2, 2 * 60, 100},
        {3, 2 * 60, 150},
        {4, 2 * 60, 50},
        {5, 2 * 60, 200},
        {6, 2 * 60, 150},
        {7, 2 * 60, 10},
        {8, 2 * 60, 50}
    };

    private enum Slot {
        ID(0), // id, a number for debug,
        DURATION(1), //duration in seconds,
        PACE(2); //p in reqs per second
        private final int index;

        private Slot(int val) {
            this.index = val;
        }
    }

    private boolean started;
    private boolean finished;
    private RateLimiter throttle;
    private final int slots[][];

    public DemandController() {
        this(DEFAULT_BATCH_SLOTS);
    }

    public DemandController(int slots[][]) {
        this.started = false;
        this.finished = false;
        this.slots = slots;
    }

    public void throttle() {
        throttle.acquire();
    }

    private void setDemand(double demand) {
        throttle.setRate(demand);
    }

    public double getDemand() {
        return throttle.getRate();
    }

    @Override
    public void run() {
        this.waitBegin();
        startSlot(0);
        for (int i = 1; i < slots.length; i++) {
            slotDuration(i - 1);
            startSlot(i);
        }
        slotDuration(slots.length - 1);
        this.finish();
    }

    public synchronized void begin() {
        throttle = RateLimiter.create(slots[0][Slot.PACE.index]);
        started = true;
        notifyAll();
    }

    public synchronized void finish() {
        finished = true;
        notifyAll();
    }

    public synchronized boolean isFinished() {
        return finished;
    }

    public synchronized void waitBegin() {
        while (!started) {
            try {
                wait();
            } catch (InterruptedException ex) {
                LOG.debug("WaitBegin Interrupted? Should not be an issue", ex);
            }
        }
    }

    private void slotDuration(int slotId) {
        try {
            TimeUnit.SECONDS.sleep(slots[slotId][Slot.DURATION.index]);
        } catch (InterruptedException ie) {
            LOG.warn("Interrupted during slot duration!", ie);
        }
    }

    private void startSlot(int slot) {
        setDemand(slots[slot][Slot.PACE.index]);
    }

}
