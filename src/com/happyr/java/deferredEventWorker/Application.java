package com.happyr.java.deferredEventWorker;

import com.happyr.java.deferredEventWorker.executors.ExecutorInterface;
import com.happyr.java.deferredEventWorker.executors.HttpExecutor;
import com.happyr.java.deferredEventWorker.executors.ShellExecutor;
import com.happyr.java.deferredEventWorker.queue.QueueInterface;
import com.happyr.java.deferredEventWorker.queue.RabbitMq;

public class Application {

    public static void main(String[] args) {

        QueueInterface mq = getQueue();
        ExecutorInterface client = getExecutor();

        String error;
        Message message;
        while (true) {
            message = new Message(mq.receive());
            error = client.execute(message);

            //TODO if there was any error
            if (error != null) {
                message.addHeader("error", error);
                mq.reportError(message.getFormattedMessage());
            }
        }
    }

    /**
     * Get a queue object from the system properties
     *
     * @return QueueInterface
     */
    private static QueueInterface getQueue() {
        String param = System.getProperty("messageQueue");
        if (param != null) {
            if (param.equalsIgnoreCase("rabbitmq")) {
                return new RabbitMq();
            }
        }
        //default
        return new RabbitMq();
    }

    /**
     * Get a executor object from the system properties
     *
     * @return ExecutorInterface
     */
    private static ExecutorInterface getExecutor() {
        String param = System.getProperty("executor");
        if (param != null) {
            if (param.equalsIgnoreCase("http")) {
                return new HttpExecutor();
            }
        }

        //default
        return new ShellExecutor();
    }
}
