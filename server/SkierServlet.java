import com.google.gson.Gson;
import dao.SkiRecordsDao;
import dao.StatisticsDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.SkiRecords;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;


@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {

  private static Logger logger = LogManager.getLogger(SkiRecordsDao.class);
  Pipeline pipeline = new Pipeline();
  StatisticsDao statisticsDao = new StatisticsDao();


  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("missing paramterers");
      out.write(jsonString);
      return;
    }

    // Get each parts of URL
    String[] urlParts = urlPath.split("/");

    // Validation of URL
    if (isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_OK);
      JSONObject newSkiLift = RpcHandler.readJSONObject(request);

      int liftID = ((Long)newSkiLift.get("liftID")).intValue();

      String uuidString = UUID.randomUUID().toString();
      String recordId = uuidString.substring(0, 8) + uuidString.substring(9, 13) +
                        uuidString.substring(14, 18) + uuidString.substring(19, 23) +
                        uuidString.substring(24);

      try {
//        long start = System.currentTimeMillis();
        SkiRecords newRecord = new SkiRecords(recordId, Integer.parseInt(urlParts[7]),
                Integer.parseInt(urlParts[1]), urlParts[3], urlParts[5],
                ((Long)newSkiLift.get("time")).intValue(),
                liftID,liftID*10);
        pipeline.enqueue(newRecord);
//        long latency =  ((System.currentTimeMillis() - start) / 1000);
//        statisticsDao.UpdateStatsTable("/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}", "POST", latency, latency);
      } catch (Exception e) {
        logger.error(e.getMessage());
      }

    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("invalid url");
      out.write(jsonString);
    }

  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("missing paramterers");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (!isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("invalid url");
      out.write(jsonString);
    } else {
      response.setStatus(HttpServletResponse.SC_OK);
      if (urlParts.length == 8) {

        try {
          long start = System.currentTimeMillis();
          int totalVertical = SkiRecordsDao.getTotalVertical(Integer.parseInt(urlParts[7]), urlParts[5]);
          if(totalVertical != 0){
            long latency = ((System.currentTimeMillis() - start) / 1000);
            statisticsDao.UpdateStatsTable("/{skierID}/vertical", "GET", latency, latency);
//            MemCache.updateStatsTable("countLatencyGet", "sumLatencyGet", "maxLatencyGet", latency);
          }
          out.write(new Gson().toJson(totalVertical));
        } catch (Exception e) {
          logger.error(e.getMessage());
        }
      } else if (urlParts.length == 3) {
        int skierId = Integer.parseInt(urlParts[1]);
        int resortID = Integer.parseInt(request.getParameter("resort"));
        String season = request.getParameter("season");
        SkiRecordsDao skiRecordsDao = SkiRecordsDao.getInstance();

        if (season != null) {
//          try{
//            HashMap<String, Integer> resultMap
//                = skiRecordsDao.getTotalVerticalWithResortAndSeason(skierId, resortID, season);
//            out.write(new Gson().toJson(resultMap));
//          } catch (SQLException e){
//            e.printStackTrace();
//          }

        } else {
//          try {
//            HashMap<String, Integer> resultMap
//                = skiRecordsDao.getTotalVerticalWithOnlyResort(skierId, resortID);
//            out.write(new Gson().toJson(resultMap));
//          } catch (SQLException e){
//            e.printStackTrace();
//          }
//        }
          }
        }
      }
    }

  private boolean isUrlValid(String[] urlPath) {
    if (urlPath.length == 8 && isInteger(urlPath[1]) && urlPath[2].equals("seasons")
        && isInteger(urlPath[3]) && urlPath[4].equals("days")
        && isInteger(urlPath[5]) && urlPath[6].equals("skiers")
        && isInteger(urlPath[7])) {
      return true;
    }
    return urlPath.length == 3 && isInteger(urlPath[1])
        && urlPath[2].equals("vertical");
  }

  private boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
    } catch(NumberFormatException e) {
      return false;
    } catch(NullPointerException e) {
      return false;
    }
    return true;
  }


}
