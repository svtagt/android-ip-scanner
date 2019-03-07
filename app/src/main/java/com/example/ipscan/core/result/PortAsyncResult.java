package com.example.ipscan.core.result;

import android.util.SparseArray;

import java.util.HashMap;

public interface PortAsyncResult {

  /**
   * Delegate to handle integer outputs
   *
   * @param host
   * @param openPortNumber
   */
  void processFinish(String host, int openPortNumber);

  /**
   * Delegate to handle boolean outputs
   *
   * @param host
   * @param success
   */
  void processFinish(String host, boolean success);

  /**
   * Delegate to handle HashMap outputs
   *
   * @param host
   * @param openPortData
   */
  void processFinish(String host, SparseArray<String> openPortData);
}
