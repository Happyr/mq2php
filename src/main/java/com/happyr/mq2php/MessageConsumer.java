package com.happyr.mq2php;

import com.happyr.mq2php.exception.MessageExecutionFailedException;
import com.happyr.mq2php.executor.ExecutorInterface;
import com.happyr.mq2php.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * Consume messages, ie give them to the executor and handle errors.
 */
public class MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(Worker.class);

    private ExecutorInterface client;

    public MessageConsumer(ExecutorInterface client) {
        this.client = client;
    }

    public void handle(Message message)
            throws MessageExecutionFailedException {
        String error = client.execute(message);

        //if there was any error
        if (error != null) {
            logger.error("Error while executing PHP script: {}", error);

            message.setHeader("error_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            message.setHeader("error", error);

            throw new MessageExecutionFailedException(message);
        }
    }
}
