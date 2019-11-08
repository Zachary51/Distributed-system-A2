package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Seasons;

public class SeasonsDao {
  private static SeasonsDao instance = null;

  public static SeasonsDao getInstance(){
    if(instance == null){
      instance = new SeasonsDao();
    }
    return instance;
  }

  public Seasons create(Seasons season) throws SQLException{
    String insertSeason = "INSERT INTO seasons(seasonId, season, resortId) VALUES(?, ?, ?);";
    Connection connection = C3poDataSource.getConnection();
    PreparedStatement insertStmt = null;
    try{
      insertStmt = connection.prepareStatement(insertSeason);
      insertStmt.setString(1, null);
      insertStmt.setString(2, season.getSeason());
      insertStmt.setInt(3, season.getResortId());
      insertStmt.executeUpdate();
      return season;
    }catch (SQLException e){
      e.printStackTrace();
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

  public List<Seasons> getSeasonsByResortId(int resortId) throws SQLException{
    String selectSeason = "SELECT * FROM resorts LEFT JOIN seasons ON resorts.resortId = "
        + "seasons.resortId WHERE resorts.resortId = ?;";
    Connection connection = C3poDataSource.getConnection();
    PreparedStatement selectStmt = null;
    ResultSet results = null;
    List<Seasons> seasons = new ArrayList<>();
    try{
      selectStmt = connection.prepareStatement(selectSeason);
      selectStmt.setInt(1, resortId);
      results = selectStmt.executeQuery();
      while(results.next()){
        int seasonId = results.getInt("seasonId");
        String season = results.getString("season");
        int resultResortId = results.getInt("resortId");
        Seasons newSeason = new Seasons(seasonId, season, resultResortId);
        seasons.add(newSeason);
      }
      return seasons;
    }catch (SQLException e){
      e.printStackTrace();
      throw e;
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
  }
}
