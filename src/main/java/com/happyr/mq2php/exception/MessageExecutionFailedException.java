package com.happyr.mq2php.exception;

import com.happyr.mq2php.message.Message;
import com.happyr.mq2php.util.Marshaller;

/**
 * Created by tobias on 2016-05-05.
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
