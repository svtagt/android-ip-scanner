package com.example.ipscan.lib.result;

import android.util.SparseArray;

public interface HostAsyncResult {

    /**
     * Delegate to handle integer outputs
     *
     * @param output
     */
    void processFinish(String ip, int output);

    /**
     * Delegate to handle boolean outputs
     *
     * @param output
     */
    void processFinish(String ip, boolean output);

    /**
     * Delegate to handle Map outputs
     *
     * @param output
     */
    void processFinish(String ip, SparseArray<String> output);
}
