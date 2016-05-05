package com.happyr.mq2php.queue;

/**
 * A interface for message queue clients
 *
 * @author Tobias Nyholm
 */
public interface QueueClient {
    /**
     * Start receiving messages
     *
     * @return Message
     */
    void receive();
}
