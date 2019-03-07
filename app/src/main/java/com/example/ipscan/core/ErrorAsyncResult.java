package com.example.ipscan.core;

interface ErrorAsyncResult {

    /**
     * Delegate to bubble up errors
     *
     * @param output
     */
    <T extends Throwable> void processFinish(T output);
}
