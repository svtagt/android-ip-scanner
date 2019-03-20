package com.example.ipscan.lib.result;

import com.example.ipscan.lib.helpers.Host;

public interface PortAsyncResult {
  /**
   * Delegate to handle timed out ports
   *
   * @param host
   * @param portNumber
   */
  void portWasTimedOut(Host host, int portNumber);

  /**
   * Delegate to handle closed ports
   *
   * @param host
   * @param portNumber
   */
  void foundClosedPort(Host host, int portNumber);

  /**
   * Delegate to handle opened ports
   *
   * @param host
   * @param portNumber
   * @param banner
   */
  void foundOpenPort(Host host, int portNumber, String banner);

  /**
   * Delegate to handle that the new one port was processed
   */
  void processItem();

  /**
   * Delegate to handle when all ports and hosts scan is complete
   *
   * @param success
   */
  void processFinish(boolean success);
}
