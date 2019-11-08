import dao.SkiRecordsDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import model.SkiRecords;

public class Pipeline {
  private LinkedBlockingQueue<SkiRecords> transactionPipeline;
  private SkiRecordsDao skiRecordsDao;
  private static final int CAPACITY = 5000;

  public Pipeline(){
    transactionPipeline = new LinkedBlockingQueue<>();
    skiRecordsDao = SkiRecordsDao.getInstance();
    start();
  }

  public void start(){
    new Thread(new Runnable(){
      @Override
      public void run(){
        while(true){
          List<SkiRecords> recordsList = dequeue(CAPACITY);
          long start = System.currentTimeMillis();
          try{
            skiRecordsDao.create(recordsList,"/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}", "POST");
          } catch (SQLException e){
            e.printStackTrace();
          }
          long latency = System.currentTimeMillis() - start;
          if(latency < 50){
            try{
              Thread.sleep(2000);
            } catch (InterruptedException e){
              e.printStackTrace();
            }
          }
        }
      }
    }).start();
  }


  public void enqueue(SkiRecords record){
    transactionPipeline.offer(record);
  }


  public List<SkiRecords> dequeue(int num){
    List<SkiRecords> records = new ArrayList<>();
    for(int i = 0; i < num && !transactionPipeline.isEmpty(); i++){
      records.add(transactionPipeline.remove());
    }
    return records;
  }
}
