package part1;

public class Constants {

  // Constants for running clients
  public static int numThreads = 32;
  public static int numSkiers = 20000;
  public static int numLifts = 40;
  public static int numRuns = 20;
  static final int DEFAULT_NUM_THREADS = 32;
  static final int MAX_NUM_THREADS = 512;
  static final int MAX_NUM_SKIERS = 50000;
  static final int MIN_SKI_LIFTS = 5;
  static final int MAX_SKI_LIFTS = 60;
  static final int DEFAULT_SKI_LIFTS = 40;
  static final int MAX_NUM_RUN = 20;
  static final int DEFAULT_NUM_RUN = 10;
  public static final int SKI_DAY_LENGTH = 420;
  static final int SHARED_DATA_RECORDS_CAPACITY = 600000;

  // Constants for connecting to server
  public static final boolean LOCAL_ENV = false;
  public static final String LOCAL_URL = "http://127.0.0.1:8080";
  // ELB
  public static final String REMOTE_URL = "http://ski-api-server-load-balancer-6705601.us-west-2.elb.amazonaws.com:8080/skidataserverapi2_war";
  //public static final String REMOTE_URL = "http://ec2-18-236-67-218.us-west-2.compute.amazonaws.com:8080/skidataserverapi2_war/";
  //public static final String REMOTE_URL = "http://ec2-54-147-242-37.compute-1.amazonaws.com:8080/CS6650WebApp/";

  // Constants for local files
  public static final String LOCAL_CSV_DIR = "/Users/zacharywong/Desktop/Computer-Science/BSDS/assignment2/src/main/java/csvFiles/";
}
