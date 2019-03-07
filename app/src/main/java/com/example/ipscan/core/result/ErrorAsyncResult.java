package com.example.ipscan.core.result;

public interface ErrorAsyncResult {

    /**
     * Delegate to bubble up errors
     *
     * @param output
     */
    <T extends Throwable> void processFinish(T output);
}
