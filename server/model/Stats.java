package model;

public class Stats {
  private String URL;
  private String requestMethod;
  private Long mean;
  private Long max;

  public Stats(String URL, String requestMethod, Long mean, Long max){
    this.URL = URL;
    this.requestMethod = requestMethod;
    this.mean = mean;
    this.max = max;
  }

  public String getURL() {
    return URL;
  }

  public void setURL(String URL) {
    this.URL = URL;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public void setRequestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
  }

  public Long getMean() {
    return mean;
  }

  public void setMean(Long mean) {
    this.mean = mean;
  }

  public Long getMax() {
    return max;
  }

  public void setMax(Long max) {
    this.max = max;
  }
}
