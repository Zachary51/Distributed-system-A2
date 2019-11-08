package model;

public class Seasons {
  protected Integer seasonId;
  protected String season;
  protected int resortId;

  public Seasons(int seasonId, String season, int resortId) {
    this.seasonId = seasonId;
    this.season = season;
    this.resortId = resortId;
  }

  public Seasons(String season, int resortId){
    this.season = season;
    this.resortId = resortId;
    this.seasonId = null;
  }

  public int getSeasonId() {
    return seasonId;
  }


  public String getSeason() {
    return season;
  }


  public int getResortId() {
    return resortId;
  }


  public String toString(){
    return "Seasons : { seasons=" + this.season + " }";
  }
}
