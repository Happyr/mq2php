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
    private String queueName;

    public Worker(String queueName, QueueClient mq) {
        this.queueName = queueName;
        this.mq = mq;
    }

    @Override
    public void run() {
        while (true) {
            try {
                mq.receive();
            } catch(Throwable t) {
                logger.error(t.getMessage());
                return;
            }
        }
    }

    public String getQueueName() {
        return queueName;
    }
}
