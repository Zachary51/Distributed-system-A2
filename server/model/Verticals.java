package model;

public class Verticals {
  protected String indexId;
  protected int vertical;

  public Verticals(String indexId, int vertical) {
    this.indexId = indexId;
    this.vertical = vertical;
  }

  public String getIndexId() {
    return indexId;
  }

  public int getVertical() {
    return vertical;
  }
}
