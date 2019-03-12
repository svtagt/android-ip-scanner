package com.example.ipscan.lib.result;

import android.util.SparseArray;

public interface PortAsyncResult {

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
   * @param openPortData
   */
  void foundOpenPort(String host, SparseArray<String> openPortData);

  /**
   * Delegate to handle boolean outputs
   *
   * @param success
   */
  void processFinish(boolean success);
}
