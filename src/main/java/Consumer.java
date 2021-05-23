import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.TextbodyApi;
import io.swagger.client.model.ErrMessage;
import io.swagger.client.model.ResultVal;
import io.swagger.client.model.TextLine;
import java.util.concurrent.BlockingQueue;

/**
 * The type Consumer.
 */
public class Consumer implements Runnable {

  private final BlockingQueue<String> queue;
  private final Count syncCountSuccess;
  private final Count syncCountFailure;
  private final TextbodyApi textbodyApi;

  /**
   * Instantiates a new Consumer.
   *
   * @param queue            the blocking queue
   * @param syncCountSuccess the successful requests counter
   * @param syncCountFailure the unsuccessful requests counter
   * @param textbodyApi      the textbody api
   */
  public Consumer(BlockingQueue<String> queue,
      Count syncCountSuccess, Count syncCountFailure, TextbodyApi textbodyApi) {
    this.queue = queue;
    this.syncCountSuccess = syncCountSuccess;
    this.syncCountFailure = syncCountFailure;
    this.textbodyApi = textbodyApi;
  }

  @Override
  public void run() {
    try {
      // continuously consume the texts
      while (true) {
        String line = this.queue.take();
        // no more texts in blocking queue
        if (line.equals("//end of the file//")) {
          throw new InterruptedException();
        }
        process(line);
      }
    } catch (InterruptedException | ApiException e) {
      Thread.currentThread().interrupt();
      System.out.println("Thread is interrupted");
    }
  }

  private void process(String line) throws ApiException {
    // constructs request body
    TextLine textLine = new TextLine();
    textLine.setMessage(line);

    // send requests to server
    ApiResponse response = textbodyApi.analyzeNewLineWithHttpInfo(textLine, "wordcount");

    // if status code is 200, print result value
    if (response.getStatusCode() == 200) {
      this.syncCountSuccess.inc();
      ResultVal resultVal = (ResultVal) response.getData();
      System.out.println(resultVal.getMessage());
    } else {
      // if status code is 4xx or 5xx, print error message
      this.syncCountFailure.inc();
      ErrMessage errMessage = (ErrMessage) response.getData();
      System.err.println(errMessage.getMessage());
    }


  }


}
