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
package eu.atmosphere.tmaf.probe;

import com.google.common.util.concurrent.RateLimiter;
import eu.atmosphere.tmaf.monitor.client.BackgroundClient;
import eu.atmosphere.tmaf.monitor.message.Data;
import eu.atmosphere.tmaf.monitor.message.Message;
import eu.atmosphere.tmaf.monitor.message.Observation;
import eu.atmosphere.tmaf.probe.util.DemandController;
import eu.atmosphere.tmaf.probe.util.Measurement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Probe to s simplify measurements gathering in the client side.
 * <p>
 * Obtains response times and throughput, global or in windows.
 *
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 * @author José Pereira <josep@dei.uc.pt>
 */
public class ClientProbe extends Thread {

    /**
     * Hard-coded Description IDs.
     * To be replaced by configuration file in the future.
     */
    public static final int CLI_THROUGHPUT_1SEC = 30;
    public static final int CLI_THROUGHPUT_TOTAL_SEC = 33;
    public static final int CLI_RT_MEAN_1SEC = 29;
    public static final int CLI_RT_STDV_1SEC = 31;
    public static final int CLI_REQS_UNDER = 59;
    public static final int CLI_CURR_DEMAND = 58;

    /**
     * Private constants.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ClientProbe.class);
    private static final double DEFAULT_MESSAGES_PER_SECOND = 1;
    private static final int DEFAULT_PARTIAL_WINDOW_LENGTH = 5;

    /**
     * Sync management.
     */
    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong messageIdSerial = new AtomicLong(0);
    /**
     * Resources information.
     */
    private final long probeId;
    private final long resourceId;
    private final String endpoint;
    /**
     * Lists for statistics.
     */
    private final DescriptiveStatistics total;
    private final LinkedList<DescriptiveStatistics> partials;
    private final LinkedList<AtomicInteger> partialsUnderContracted;

    /**
     * Experiment Management
     */
    private final DemandController demand;
    private final Measurement measure;

    /**
     * Properties for statistics.
     */
    private final double messagesPerSecond;
    private final int partialWindowLength;
    private long startingTotalTimeId;
    private final long contractedResponseTime;

    public ClientProbe(long probeId, long resourceId, String endpoint,
            long contractedResponseTime, DemandController demand) {
        this(probeId, resourceId, endpoint, contractedResponseTime, demand,
                DEFAULT_MESSAGES_PER_SECOND, DEFAULT_PARTIAL_WINDOW_LENGTH);
    }

    public ClientProbe(long probeId, long resourceId, String endpoint,
            long contractedResponseTime, DemandController demand,
            double messagesPerSecond, int partialWindowLength) {
        this.measure = new Measurement();
        this.total = new DescriptiveStatistics();
        this.partials = new LinkedList<>();
        this.partialsUnderContracted = new LinkedList<>();
        this.probeId = probeId;
        this.resourceId = resourceId;
        this.endpoint = endpoint;
        this.messagesPerSecond = messagesPerSecond;
        this.partialWindowLength = partialWindowLength;
        this.contractedResponseTime = contractedResponseTime;
        this.demand = demand;
    }

    @Override
    public void start() {
        lock.lock();
        try {
            this.total.clear();
            this.partials.clear();
            this.partials.add(new DescriptiveStatistics());
            this.partialsUnderContracted.clear();
            this.partialsUnderContracted.add(new AtomicInteger(0));
            this.startingTotalTimeId = measure.begin();
            running.set(true);
        } finally {
            lock.unlock();
        }
        super.start();
    }

    public void shutdown() {
        running.set(false);
    }

    @Override
    public void run() {
        final RateLimiter probeThrottle = RateLimiter.create(messagesPerSecond);

        final BackgroundClient bc = new BackgroundClient(endpoint);
        bc.start();

        while (running.get()) {
            probeThrottle.acquire();
            bc.send(this.buildMessage());
        }
    }

    private Message buildMessage() {
        List<Data> data = new ArrayList<>();
        long messageId = messageIdSerial.incrementAndGet();
        long time = getProbeTime();

        long duration = measure.partial(startingTotalTimeId, TimeUnit.SECONDS);

        // cuts a new partial. Only thing that should run inside a lock() block;
        this.newPartial();

        int partialN = 0;
        int partialNUnderContracted = 0;

        for (int i = 1; i <= partialWindowLength && i < partials.size(); i++) {
            // this line should stay, to compute the number of elements
            partialN += partials.get(i).getN();
            partialNUnderContracted += partialsUnderContracted.get(i).get();
        }

        double partialDoubleArray[] = new double[partialN];
        int index = 0;

        for (int i = 1; i <= partialWindowLength && i < partials.size(); i++) {
            double[] values = partials.get(i).getValues();
            System.arraycopy(values, 0, partialDoubleArray, index, values.length);
            index += values.length;
        }

        DescriptiveStatistics partial = new DescriptiveStatistics(partialDoubleArray);

        long tput = partialN / 5;
        double reqsUnderContract = (double) partialNUnderContracted / (double) partialN;

        // throughput of the last second (in requests per second)
        data.add(new Data(Data.Type.MEASUREMENT, CLI_THROUGHPUT_1SEC, new Observation(time, tput)));

        // average throughput since start of measurement (in requests per second)
        double avgtput = (double) total.getN() / duration;
        data.add(new Data(Data.Type.MEASUREMENT, CLI_THROUGHPUT_TOTAL_SEC, new Observation(time, avgtput)));

        // mean response time in the last second (in millis)
        data.add(new Data(Data.Type.MEASUREMENT, CLI_RT_MEAN_1SEC, new Observation(time, partial.getMean())));

        // STDV of response time in the last second (in millis)
        data.add(new Data(Data.Type.MEASUREMENT, CLI_RT_STDV_1SEC, new Observation(time, partial.getStandardDeviation())));

        // Rate of Requests under contract
        // In the future maybe send something more raw (without dividing)
        data.add(new Data(Data.Type.MEASUREMENT, CLI_REQS_UNDER, new Observation(time, reqsUnderContract)));

        // Current demand 
        data.add(new Data(Data.Type.MEASUREMENT, CLI_CURR_DEMAND, new Observation(time, demand.getDemand())));

        //        // mean duration since start of measurement (in millis)
//        data.add(new Data(Data.Type.MEASUREMENT, /*TBD*/ 9999013, new Observation(time, total.getMean())));
//
//        // STDV of duration since start of measurement (in millis)
//        data.add(new Data(Data.Type.MEASUREMENT, /*TBD*/ 9999014, new Observation(time, total.getStandardDeviation())));
        return new Message(probeId, resourceId, messageId, time, data);
    }

    private void newPartial() {
        lock.lock();
        try {
            this.partials.add(0, new DescriptiveStatistics());
            this.partialsUnderContracted.add(0, new AtomicInteger(0));
        } finally {
            lock.unlock();
        }
    }

    public void post(long duration) {
        lock.lock();
        try {
            partials.get(0).addValue(duration);
            if (duration <= contractedResponseTime) {
                partialsUnderContracted.get(0).incrementAndGet();
            }
            total.addValue(duration);
        } finally {
            lock.unlock();
        }
    }

    private long getProbeTime() {
        return Instant.now().getEpochSecond();
    }
}
