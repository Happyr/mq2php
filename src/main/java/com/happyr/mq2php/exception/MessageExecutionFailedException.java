package com.happyr.mq2php.exception;

import com.happyr.mq2php.message.Message;
import com.happyr.mq2php.util.Marshaller;

/**
 * An exception that is thrown by the executors if something unexpected happened.
 */
public class MessageExecutionFailedException extends RuntimeException {
    private String error;

    public MessageExecutionFailedException(Message message) {
        error = new String(Marshaller.toBytes(message));
    }

    @Override
    public String getMessage() {
        return error;
    }
}
