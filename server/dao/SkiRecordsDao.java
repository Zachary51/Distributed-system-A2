package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.SkiRecords;
import model.Verticals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SkiRecordsDao {

  private static SkiRecordsDao instance = null;
  private static Logger logger = LogManager.getLogger(SkiRecordsDao.class);

  public static SkiRecordsDao getInstance(){
    if(instance == null){
      instance = new SkiRecordsDao();
    }
    return instance;
  }

  private SkiRecordsDao(){
  }

  public static void create(List<SkiRecords> skiRecords, String URL, String requestType) throws SQLException{
    String insertRecord = "INSERT INTO skiRecords(recordId,skierId, resortId, season, dayId, skiTime,"
        + "LiftId, vertical) "
        + "VALUES(?,?,?,?,?,?,?,?);";
    // Update stats
    String select = "SELECT COUNT(*), AVG(mean), MAX(max) FROM statistics WHERE (url=?) and (requestType=?);";
    String delete = "DELETE FROM statistics WHERE (url=?) and (requestType=?);";
    String insert = "INSERT INTO statistics(url, requestType, mean, max) VALUES(?,?,?,?);";
    long start = System.currentTimeMillis();
    Connection connection = C3poDataSource.getConnection();
    ResultSet results = null;
    PreparedStatement insertStmt = null;

    try{
      insertStmt = connection.prepareStatement(insertRecord);
      for(SkiRecords skiRecord : skiRecords) {
        insertStmt.setString(1, skiRecord.getRecordId());
        insertStmt.setInt(2, skiRecord.getSkierId());
        insertStmt.setInt(3, skiRecord.getResortId());
        insertStmt.setString(4, skiRecord.getSeasonId());
        insertStmt.setString(5, skiRecord.getDayId());
        insertStmt.setInt(6, skiRecord.getSkiTime());
        insertStmt.setInt(7, skiRecord.getLiftId());
        insertStmt.setInt(8, skiRecord.getVertical());
        insertStmt.addBatch();
      }
      insertStmt.executeBatch();
      long latency = System.currentTimeMillis() - start;
      // Update stats
      long count = 0L;
      long curMean = 0L;
      long curMax = 0L;
      insertStmt = connection.prepareStatement(select);
      insertStmt.setString(1, URL);
      insertStmt.setString(2, requestType);
      results = insertStmt.executeQuery();
      if(results.next()){
        count = results.getLong(1);
        curMean = results.getLong(2);
        curMax = results.getLong(3);
      }
      if(count > 1000){
        insertStmt = connection.prepareStatement(delete);
        insertStmt.setString(1, URL);
        insertStmt.setString(2, requestType);
        insertStmt.executeUpdate();
      }
      curMean = (curMean * count + latency) / (count + 1L);
      curMax = Math.max(curMax, latency);
      insertStmt = connection.prepareStatement(insert);
      insertStmt.setString(1, URL);
      insertStmt.setString(2, requestType);
      insertStmt.setLong(3, curMean);
      insertStmt.setLong(4, curMax);
      insertStmt.executeUpdate();

    } catch (SQLException e){
      logger.error(e.getMessage());
    } finally {
      if(connection != null){
        connection.close();
      }
      if(insertStmt != null){
        insertStmt.close();
      }
    }
  }

  public Verticals insertVertical(Verticals vertical) throws SQLException{
    String insertVertical = "INSERT INTO verticals(indexId, vertical) VALUES(?,?);";
    Connection connection = C3poDataSource.getConnection();
    PreparedStatement insertStmt = null;
    try{
      insertStmt = connection.prepareStatement(insertVertical);
      insertStmt.setString(1, vertical.getIndexId());
      insertStmt.setInt(2, vertical.getVertical());
      insertStmt.execute();
      return vertical;
    } catch (SQLException e){
      logger.error(e.getMessage());
    } finally {
      if(connection != null){
        connection.close();
      }
      if(insertStmt != null){
        insertStmt.close();
      }
    }
    return null;
  }

  public Verticals updateVertical(Verticals vertical) throws SQLException{
    String updateVerticalSum = "UPDATE verticals SET vertical=? WHERE indexId=?;";
    Connection connection = C3poDataSource .getConnection();
    PreparedStatement updateStmt = null;
    try{
      updateStmt = connection.prepareStatement(updateVerticalSum);
      updateStmt.setInt(1, vertical.getVertical());
      updateStmt.setString(2, vertical.getIndexId());
      updateStmt.executeUpdate();
      return vertical;
    } catch (SQLException e){
      e.printStackTrace();
    } finally {
      if(connection != null){
        connection.close();
      }
      if(updateStmt != null){
        updateStmt.close();
      }
    }
    return null;
  }

  public int getVerticalFromVerticalsTable(String indexId) throws SQLException{
    String getVerticalSum = "SELECT vertical FROM verticals WHERE indexId=?;";
    Connection connection = C3poDataSource.getConnection();
    PreparedStatement selectStmt = null;
    ResultSet resultSet = null;
    int verticalSum = 0;
    try{
      selectStmt = connection.prepareStatement(getVerticalSum);
      selectStmt.setString(1, indexId);
      resultSet = selectStmt.executeQuery();
      if(resultSet.next()){
        verticalSum = resultSet.getInt(1);
      }
      return verticalSum;
    } catch (SQLException e){
      e.printStackTrace();
    } finally {
      if(connection != null) {
        connection.close();
      }
      if(selectStmt != null) {
        selectStmt.close();
      }
      if(resultSet != null) {
        resultSet.close();
      }
    }
    return 0;
  }

  // get the total vertical for the skier for the specified ski day
  public static int getTotalVertical(int skierId, String dayId) throws SQLException{
    String getVerticalSum = "SELECT SUM(vertical) FROM skiRecords WHERE "
        + "skiRecords.skierId = ? AND skiRecords.dayId = ?;";
    Connection connection = C3poDataSource.getConnection();
    PreparedStatement selectStmt = null;
    ResultSet results = null;
    int verticalSum = 0;
    try{
      selectStmt = connection.prepareStatement(getVerticalSum);
      selectStmt.setInt(1, skierId);
      selectStmt.setString(2, dayId);
      results = selectStmt.executeQuery();
      if(results.next()){
        verticalSum = results.getInt(1);
      }
      return verticalSum;
    } catch (SQLException e){
      logger.error(e.getMessage());
    } finally {
      if(connection != null) {
        connection.close();
      }
      if(selectStmt != null) {
        selectStmt.close();
      }
      if(results != null) {
        results.close();
      }
    }
    return 0;
  }

  // get the total vertical for the skier the specified resort. If no season is specified, return all seasons
//  public HashMap<String, Integer> getTotalVerticalWithOnlyResort(int skierId, int resortId) throws SQLException{
//    HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
//    String getVerticalSum = "SELECT season, SUM(vertical) FROM skiRecords WHERE skierId=? AND resortId=?"
//        + " GROUP BY skiRecords.season;";
//    Connection connection = C3poDataSource.getConnection();
//    PreparedStatement selectStmt = null;
//    ResultSet results = null;
//    try{
//      selectStmt = connection.prepareStatement(getVerticalSum);
//      selectStmt.setInt(1, skierId);
//      selectStmt.setInt(2, resortId);
//      results = selectStmt.executeQuery();
//      while(results.next()){
//        String season = results.getString("season");
//        int totalVertical = results.getInt("SUM(vertical)");
//        resultMap.put(season, totalVertical);
//      }
//      return resultMap;
//    } catch (SQLException e){
//      logger.error(e.getMessage());
//    } finally {
//      if(connection != null) {
//        connection.close();
//      }
//      if(selectStmt != null) {
//        selectStmt.close();
//      }
//      if(results != null) {
//        results.close();
//      }
//    }
//  }

//  public Map<String, Integer> getTotalVerticalWithResortAndSeason(int skierId, int resortId, String season) throws SQLException{
//    Map<String, Integer> resultMap = new HashMap<>();
//    String getVerticalSum = "SELECT season, SUM(vertical) FROM skiRecords WHERE skierId=? AND resortId=? AND season=?;";
//    Connection connection = C3poDataSource.getConnection();
//    PreparedStatement selectStmt = null;
//    ResultSet results = null;
//    try{
//      selectStmt = connection.prepareStatement(getVerticalSum);
//      selectStmt.setInt(1, skierId);
//      selectStmt.setInt(2, resortId);
//      selectStmt.setString(3, season);
//      results = selectStmt.executeQuery();
//
//      if(results.next()){
//        String resultSeason = results.getString("season");
//        int vertical = results.getInt("SUM(vertical)");
//        resultMap.put(resultSeason, vertical);
//      }
//
//      return resultMap;
//    } catch (SQLException e){
//      logger.error(e.getMessage());
//    } finally {
//      if(connection != null) {
//        connection.close();
//      }
//      if(selectStmt != null) {
//        selectStmt.close();
//      }
//      if(results != null) {
//        results.close();
//      }
//    }
//    return resultMap;
//  }
//

}
