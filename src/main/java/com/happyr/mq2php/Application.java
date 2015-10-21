package com.happyr.mq2php;

import com.happyr.mq2php.executors.ExecutorInterface;
import com.happyr.mq2php.executors.FastCgiExecutor;
import com.happyr.mq2php.executors.ShellExecutor;
import com.happyr.mq2php.queue.QueueInterface;
import com.happyr.mq2php.queue.RabbitMq;

/**
 * @author Tobias Nyholm
 */
public class Application {

    public static void main(String[] args) {
        int nbThreads = 5;
        if (args.length > 0) {
            nbThreads = Integer.parseInt(args[0]);
        }
        QueueInterface mq = getQueue();
        ExecutorInterface client = getExecutor();

        for (int i = 0; i < nbThreads; i++) {
            new Worker(mq, client).start();
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
            if (param.equalsIgnoreCase("fastcgi")) {
                return new FastCgiExecutor();
            }
        }

        //default
        return new ShellExecutor();
    }
}
