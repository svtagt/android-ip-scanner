package com.example.ipscan.lib.result;

public interface PortAsyncResult {
  //TODO make right comments

  /**
   * Delegate to handle integer outputs
   *
   * @param host
   * @param portNumber
   */
  void portWasTimedOut(String host, int portNumber);

  /**
   * Delegate to handle integer outputs
   *
   * @param host
   * @param portNumber
   */
  void foundClosedPort(String host, int portNumber);

  /**
   * Delegate to handle HashMap outputs
   *
   * @param host
   * @param portNumber
   * @param banner
   */
  void foundOpenPort(String host, int portNumber, String banner);

  /**
   * Delegate to ...
   *
   */
  void processItem();

  /**
   * Delegate to handle boolean outputs
   *
   * @param success
   */
  void processFinish(boolean success);
}
