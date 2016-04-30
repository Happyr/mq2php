package com.happyr.mq2php;

import com.happyr.mq2php.executor.ExecutorInterface;
import com.happyr.mq2php.message.Message;
import com.happyr.mq2php.queue.QueueClient;
import com.happyr.mq2php.util.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * com.happyr.java.deferredEventWorker
 *
 * @author Tobias Nyholm
 */
public class Worker extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(Worker.class);

    private QueueClient mq;
    private ExecutorInterface client;

    public Worker(QueueClient mq, ExecutorInterface client) {
        this.mq = mq;
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            try {
                listenToQueue();
            } catch(Throwable t) {
                logger.error(t.getMessage());
            }
        }
    }

    private void listenToQueue() {
        Message message = mq.receive();
        if (message == null) {
            return;
        }

        String error = client.execute(message);

        //if there was any error
        if (error != null) {
            logger.error("Error while executing PHP script: {}", error);

            message.setHeader("error_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            message.setHeader("error", error);

            mq.reportError(new String(Marshaller.toBytes(message)));
        }
    }
}
