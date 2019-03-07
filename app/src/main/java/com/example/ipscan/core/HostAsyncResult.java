package com.example.ipscan.core;

import android.util.SparseArray;

interface HostAsyncResult {

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
