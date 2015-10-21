package com.happyr.mq2php.executors;

import com.happyr.mq2php.Message;

/**
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
