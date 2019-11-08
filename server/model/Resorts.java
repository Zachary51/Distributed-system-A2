package model;

public class Resorts {
  protected int resortId;
  protected String resortName;

  public Resorts(int resortId, String resortName){
    this.resortId = resortId;
    this.resortName = resortName;
  }

  public int getResortId() {
    return resortId;
  }


  public String getResortName() {
    return resortName;
  }


  public String toString(){
    return "Resort : { resortId=" + this.resortId + " name=" + this.resortName + " }";
  }
}
