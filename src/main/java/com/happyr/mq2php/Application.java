package com.happyr.mq2php;

import com.happyr.mq2php.executors.ExecutorInterface;
import com.happyr.mq2php.executors.FastCgiExecutor;
import com.happyr.mq2php.executors.ShellExecutor;
import com.happyr.mq2php.queue.QueueInterface;
import com.happyr.mq2php.queue.RabbitMq;

import java.util.Vector;

/**
 * @author Tobias Nyholm
 */
public class Application {

    public static void main(String[] args) {
        int nbThreads = 5;
        if (args.length > 0) {
            nbThreads = Integer.parseInt(args[0]);
        }

        Vector<QueueInterface> queues = getQueues();
        ExecutorInterface client = getExecutor();
        int queueLength = queues.size();

        // Make sure we have at least as many threads as queues.
        if (nbThreads < queueLength) {
            nbThreads = queueLength;
        }

        for (int i = 0; i < nbThreads; i++) {
            new Worker(queues.get(i % queueLength), client).start();
        }
    }

    /**
     *
     * @return Vector<QueueInterface>
     */
    private static Vector<QueueInterface> getQueues() {
        // This is a comma separated string
        String queueNamesArg = System.getProperty("queueNames");
        if (queueNamesArg == null) {
            queueNamesArg = "sf_deferred_events";
        }
        String[] queueNames = queueNamesArg.split(",");

        Vector<QueueInterface> queues = new Vector<QueueInterface>();
        for (String name:queueNames) {
            queues.add(getQueue(name));
        }
        return queues;
    }

    /**
     * Get a queue object from the system properties
     *
     * @return QueueInterface
     */
    private static QueueInterface getQueue(String queueName) {
        String param = System.getProperty("messageQueue");
        if (param == null) {
            //default
            param = "rabbitmq";
        }

        if (param.equalsIgnoreCase("rabbitmq")) {
            return new RabbitMq(queueName);
        }

        throw new IllegalArgumentException("Could not find QueueInterface implementation named " + param);
    }

    /**
     * Get a executor object from the system properties
     *
     * @return ExecutorInterface
     */
    private static ExecutorInterface getExecutor() {
        String param = System.getProperty("executor");
        if (param == null) {
            //default
            param = "shell";
        }

        if (param.equalsIgnoreCase("fastcgi")) {
            return new FastCgiExecutor();
        }

        if (param.equalsIgnoreCase("shell")) {
            return new ShellExecutor();
        }

        throw new IllegalArgumentException("Could not find ExecutorInterface implementation named " + param);
    }
}
