package part1;


import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import part2.PerformanceStats;

public class MultiThreadRunner {

  private int numThreads;
  private int numSkiers;
  private int numLifts;
  private int numRuns;
  private Logger logger = LogManager.getLogger(PhaseThread.class.getName());
//  private static AtomicInteger numSuccessfulRequest;
  private static AtomicInteger numTotalRequest;
  private static AtomicInteger numFailedRequest;
  private BlockingQueue<SharedData> records = new ArrayBlockingQueue<>(Constants.SHARED_DATA_RECORDS_CAPACITY);

  public MultiThreadRunner(int numThreads, int numSkiers, int numLifts, int numRuns) {
    this.numThreads = numThreads;
    this.numSkiers = numSkiers;
    this.numLifts = numLifts;
    this.numRuns = numRuns;
  }


  public void run(){


//    numSuccessfulRequest = new AtomicInteger();
    numTotalRequest = new AtomicInteger();
    numFailedRequest = new AtomicInteger();
    // Construct CountDownLatch for controlling the starting time of different phases
    int startupPhaseThreadsNum = this.numThreads / 4;
    int peakPhaseThreadsNum = this.numThreads;
    int coolDownPhaseThreadsNum = this.numThreads / 4;
    CountDownLatch startUpPhaseLatch = new CountDownLatch(1);
    CountDownLatch peakPhaseLatch = new CountDownLatch((int)(startupPhaseThreadsNum * 0.1));
    CountDownLatch coolDownPhaseLatch = new CountDownLatch((int)(peakPhaseThreadsNum * 0.1));
    CountDownLatch totalLatch = new CountDownLatch(startupPhaseThreadsNum + peakPhaseThreadsNum + coolDownPhaseThreadsNum);

    // start up phase
    ExecutorService startupThreadsPool = Executors.newFixedThreadPool(this.numThreads/4);
    for (int i = 0; i < this.numThreads / 4; i++) {
      int idRange = this.numSkiers / (this.numThreads / 4);
      int runTimes = (int) (this.numRuns * 0.1 * idRange);
      int startTime = 0;
      int endTime = 90;
      Runnable startupThread = new PhaseThread(idRange * i, idRange * i + idRange,
          startTime, endTime, runTimes, startUpPhaseLatch, peakPhaseLatch, this.records, numLifts, totalLatch, numFailedRequest);
      startupThreadsPool.execute(startupThread);
      numTotalRequest.addAndGet(runTimes);
    }

    // Peak Phase
    ExecutorService peakThreadsPool = Executors.newFixedThreadPool(this.numThreads);


    for (int i = 0; i < numThreads; i++) {
      int startTime = 91;
      int endTime = 360;
      int idRange = this.numSkiers / this.numThreads;
      int runTimes = (int) (numRuns * 0.8) * idRange;
        Runnable PeakThread = new PhaseThread(idRange * i, idRange * i + idRange,
            startTime, endTime, runTimes, peakPhaseLatch, coolDownPhaseLatch, records, numLifts, totalLatch, numFailedRequest);
        peakThreadsPool.execute(PeakThread);
        numTotalRequest.addAndGet(runTimes);
      }

      // Cooldown Phase
    ExecutorService coolDownThreadsPool = Executors.newFixedThreadPool(this.numThreads/4);
    for (int i = 0; i < this.numThreads / 4; i++) {
      int startTime = 361;
      int endTime = 420;
      int idRange = this.numSkiers/ (this.numThreads / 4);
      int runTimes = (int) (this.numRuns * 0.1 *idRange);
        Runnable coolDownThread = new PhaseThread(idRange * i, idRange * i + idRange, startTime,
            endTime, runTimes, coolDownPhaseLatch, null, this.records, numLifts, totalLatch, numFailedRequest);
      coolDownThreadsPool.execute(coolDownThread);
      numTotalRequest.addAndGet(runTimes * 2);
    }


    startUpPhaseLatch.countDown();
    long wallStart = System.currentTimeMillis();
    System.out.println("Start processing at " + new Date(wallStart).toString());

    try{
      totalLatch.await();
    } catch (InterruptedException e){
      Thread.currentThread().interrupt();
      e.printStackTrace();
    } finally {
      startupThreadsPool.shutdown();
      peakThreadsPool.shutdown();
      coolDownThreadsPool.shutdown();
    }


    long wallEnd = System.currentTimeMillis();
    System.out.println("Stop processing at " + new Date(wallEnd).toString());

    // Print the performance stats data
    PerformanceStats performanceStats =
        new PerformanceStats(wallEnd - wallStart, this.records, Constants.numThreads,
            numTotalRequest, numFailedRequest);
    performanceStats.printStats();
  }
}
