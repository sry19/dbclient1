import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * The type Reader.
 */
public class Reader implements Runnable {

  private final BlockingQueue<String> queue;
  private final int numOfThreads;
  private final FileReader fileReader;

  /**
   * Instantiates a new Reader.
   *
   * @param queue        the blocking queue
   * @param numOfThreads the num of consumer threads
   * @param fileReader   the file reader
   */
  public Reader(BlockingQueue<String> queue, int numOfThreads, FileReader fileReader) {
    this.queue = queue;
    this.numOfThreads = numOfThreads;
    this.fileReader = fileReader;
  }

  @Override
  public void run() {
    try {
      process();
    } catch (InterruptedException | IOException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void process() throws InterruptedException, IOException {
    // read texts from files
    BufferedReader reader;
    reader = new BufferedReader(this.fileReader);

    String line = reader.readLine();
    while (line != null) {
      // ignore empty line
      if (!line.equals("")) {
        this.queue.put(line);
      }
      line = reader.readLine();
    }

    // finish reading
    reader.close();

    // put end of the file sign
    for (int i = 0; i < this.numOfThreads; i++) {
      this.queue.put("//end of the file//");
    }

  }

}
