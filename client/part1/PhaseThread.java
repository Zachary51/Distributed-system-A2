package part1;

import com.squareup.okhttp.OkHttpClient;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseThread implements Runnable{
  private int startSkierId;
  private int endSkierId;
  private int startTime;
  private int endTime;
  private int runTimes;
  private int numLifts;
  private AtomicInteger numOfFailedRequest;
  private CountDownLatch currentCountdownLatch;
  private CountDownLatch nextPhaseCountdownLatch;
  private CountDownLatch totalLatch;
  private BlockingQueue<SharedData> sharedRecords;
  public static final Logger logger = LogManager.getLogger(PhaseThread.class.getName());


  public PhaseThread(int startSkierId, int endSkierId,
      int startTime, int endTime, int runTimes, CountDownLatch currentLatch, CountDownLatch nextLatch,
      BlockingQueue<SharedData> sharedRecords, int numLifts, CountDownLatch totalLatch, AtomicInteger numOfFailedRequest){
    this.startSkierId = startSkierId;
    this.endSkierId = endSkierId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.runTimes = runTimes;
    this.currentCountdownLatch = currentLatch;
    this.nextPhaseCountdownLatch = nextLatch;
    this.sharedRecords = sharedRecords;
    this.numLifts = numLifts;
    this.totalLatch = totalLatch;
    this.numOfFailedRequest = numOfFailedRequest;
  }

  @Override
  public void run() {

    try{
      currentCountdownLatch.await();
    } catch (InterruptedException e){
      Thread.currentThread().interrupt();
      e.printStackTrace();
    }

    String targetUrl =  Constants.LOCAL_ENV ? Constants.LOCAL_URL : Constants.REMOTE_URL;
    SkiersApi apiInstance = new SkiersApi();
    ApiClient apiClient = apiInstance.getApiClient();
    apiClient.setBasePath(targetUrl);

    long start = System.currentTimeMillis();

    for(int i = 0; i < runTimes; i++) {
      int randomSkierId = ThreadLocalRandom.current().nextInt(startSkierId , endSkierId+ 1);
      int resortId = ThreadLocalRandom.current().nextInt(100);
      String seasonId = String.valueOf(ThreadLocalRandom.current().nextInt(2019 - 2000 + 1));
      String dayId = String.valueOf(ThreadLocalRandom.current().nextInt(366));
      LiftRide ride = new LiftRide();
      ride.setLiftID(numLifts);
      ride.setTime(ThreadLocalRandom.current().nextInt(endTime - startTime + 1));

      try {
        apiInstance.writeNewLiftRideWithHttpInfo(ride, resortId, seasonId, dayId, randomSkierId);
      } catch (ApiException e) {
        if (e.getCode() == 404 || e.getCode() == 400) {
          this.numOfFailedRequest.getAndIncrement();
        }
        e.printStackTrace();
      }

      //Phase 3 modification
      if (nextPhaseCountdownLatch == null) {
        try {
          apiInstance.getSkierDayVerticalWithHttpInfo(resortId, seasonId, dayId, randomSkierId);
        } catch (ApiException e) {
          if (e.getCode() == 404 || e.getCode() == 400) {
            this.numOfFailedRequest.getAndIncrement();
          }
        }
      }
    }

    long latency = System.currentTimeMillis() - start;
    this.sharedRecords.add(new SharedData(start, latency, 200));

    if(this.nextPhaseCountdownLatch != null){
      this.nextPhaseCountdownLatch.countDown();
    }
    totalLatch.countDown();

  }
}
