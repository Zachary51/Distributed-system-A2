package model;

public class SkiRecords {
  protected int skierId;
  protected int resortId;
  protected String seasonId;
  protected String dayId;
  protected int skiTime;
  protected int liftId;
  protected int vertical;
  protected String recordId;

  public SkiRecords(String recordId, int skierId, int resortId, String seasonId, String dayId, int skiTime, int liftId,
      int vertical) {
    this.skierId = skierId;
    this.resortId = resortId;
    this.seasonId = seasonId;
    this.dayId = dayId;
    this.skiTime = skiTime;
    this.liftId = liftId;
    this.vertical = vertical;
    this.recordId = recordId;
  }

  public int getSkierId() {
    return skierId;
  }


  public int getResortId() {
    return resortId;
  }


  public String getSeasonId() {
    return seasonId;
  }


  public String getDayId() {
    return dayId;
  }


  public int getSkiTime() {
    return skiTime;
  }


  public int getLiftId() {
    return liftId;
  }


  public int getVertical() {
    return vertical;
  }



  public String getRecordId() {
    return recordId;
  }
}
