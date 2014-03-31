package com.happyr.java.deferredEventWorker.executors;

import com.happyr.java.deferredEventWorker.Message;

/**
 *
 * @author Tobias Nyholm
 */
public interface ExecutorInterface {
    /**
     * Execute the message payload.
     *
     * @param message
     * @return null|String with errors. Null if everything was okey
     */
    String execute(Message message);
}
