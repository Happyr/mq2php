package com.happyr.java.deferredEventWorker.queue;

/**
 * A interface for message queue clients
 *
 * @author Tobias Nyholm
 */
public interface QueueInterface {
    /**
     * Get a message from the queue. This should be blocking
     *
     * @return String
     */
    public String receive();

    /**
     * Report an error. Add a message to an error queue/topic
     *
     * @param message
     * @return boolean
     */
    public boolean reportError(String message);
}
