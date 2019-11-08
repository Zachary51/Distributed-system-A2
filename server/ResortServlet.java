import com.google.gson.Gson;
import dao.ResortsDao;
import dao.SeasonsDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Resorts;
import model.Seasons;
import org.json.simple.JSONObject;

@WebServlet(name = "ResortServlet")
public class ResortServlet extends HttpServlet {

  protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("missing paramterers");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_OK);
      int resortId = Integer.parseInt(urlParts[1]);
      JSONObject newSeasonObject = RpcHandler.readJSONObject(request);
      String newSeason = String.valueOf(newSeasonObject.get("year"));
      SeasonsDao seasonsDao = SeasonsDao.getInstance();
      try {
        seasonsDao.create(new Seasons(newSeason, resortId));
      } catch (SQLException e){
        e.printStackTrace();
      }

    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("invalid");
      out.write(jsonString);
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    String[] urlParts = urlPath.split("/");

    if (!isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("invalid url");
      out.write(jsonString);
    } else {

      if (urlParts.length == 0){

        // get a list of ski resorts in the database
        ResortsDao resortsDao = ResortsDao.getInstance();
        try{
          List<Resorts> resultResorts = resortsDao.getAllResorts();
          out.write(new Gson().toJson(resultResorts));
        } catch (SQLException e){
          e.printStackTrace();
        }

      } else {
        // get a list of seasons for the specified resort
        SeasonsDao seasonsDao = SeasonsDao.getInstance();
        int resortId = Integer.parseInt(urlParts[1]);
        System.out.println(resortId);
        try{
          List<Seasons> resultSeasons = seasonsDao.getSeasonsByResortId(resortId);
          out.write(new Gson().toJson(resultSeasons));
        } catch (SQLException e){
          e.printStackTrace();
        }
      }
      response.setStatus(HttpServletResponse.SC_OK);

    }
  }

  private boolean isUrlValid(String[] urlPath) {
    if (urlPath.length == 0) {
      return true;
    }
    else if (urlPath.length == 3 && isInteger(urlPath[1])
        && urlPath[2].equals("seasons")) {
      return true;
    }
    return false;
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
