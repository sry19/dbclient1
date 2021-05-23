/**
 * The type Count.
 */
public class Count {

  private Integer count;

  /**
   * Instantiates a new Count.
   *
   * @param count the count
   */
  public Count(Integer count) {
    this.count = count;
  }

  /**
   * Inc.
   */
  synchronized public void inc() {
    this.count++;
  }

  /**
   * Gets count.
   *
   * @return the count
   */
  public Integer getCount() {
    return this.count;
  }

}
