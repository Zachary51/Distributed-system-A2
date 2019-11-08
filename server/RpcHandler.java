
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class RpcHandler {
  public static void writeJsonObject(HttpServletResponse response, JsonObject obj) throws IOException{
    response.setContentType("application/json");
    response.setHeader("Access-Control-Allow-Origin", "*");
    PrintWriter out = response.getWriter();
    out.print(obj);
    out.close();
  }

  public static JSONObject readJSONObject(HttpServletRequest request){
    StringBuilder stringBuilder = new StringBuilder();
    try(BufferedReader reader = request.getReader()){
      String line = null;
      while((line = reader.readLine()) != null){
        stringBuilder.append(line);
      }
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(stringBuilder.toString());
      return object;
    } catch (Exception e){
      e.printStackTrace();
    }
    return new JSONObject();
  }

}
