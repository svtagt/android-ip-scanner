package com.example.ipscan.core.result;

public interface ErrorAsyncResult {

    /**
     * Delegate to bubble up errors
     *
     * @param err
     */
    <T extends Throwable> void processFinish(T err);
}
