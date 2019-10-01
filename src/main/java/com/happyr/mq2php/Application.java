package com.happyr.mq2php;

import com.happyr.mq2php.executor.ExecutorInterface;
import com.happyr.mq2php.executor.FastCgiExecutor;
import com.happyr.mq2php.executor.HttpExecutor;
import com.happyr.mq2php.executor.ShellExecutor;
import com.happyr.mq2php.queue.QueueClient;
import com.happyr.mq2php.queue.RabbitMqClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author Tobias Nyholm
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static ArrayList<Worker> workers = new ArrayList<Worker>();

    public static void main(String[] args) {
        int nbThreads = getNumberOfThreads();
        String[] queueNames = getQueueNames();

        logger.info("Starting mq2php listening to {} queues.", queueNames.length);

        // Start all queue
        for (int i = 0; i < queueNames.length; i++) {
            for (int j = 0; j < nbThreads; j++) {
                String name = queueNames[i];
                startNewWorker(name);
            }
        }

        // Start monitor their health
        checkWorkerHealth();
    }

    /**
     * Check the heath of each worker periodically.
     */
    private static void checkWorkerHealth() {
        Worker worker;
        String queueName;
        while (true) {
            for (int i = 0; i < workers.size(); i++) {
                worker = workers.get(i);
                if (!worker.isAlive()) {
                    queueName = worker.getQueueName();
                    workers.remove(worker);
                    startNewWorker(queueName);
                }
            }

            try {
                // Sleep for a minute
                Thread.sleep(60000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Start a new worker and put it to the worker queue
     *
     * @param name of the queue that we should listen to
     */
    private static void startNewWorker(String name) {
        logger.info("Starting worker for queue '{}'", name);
        Worker worker = new Worker(name, getQueue(name));
        worker.start();
        workers.add(worker);
    }

    /**
     * Get the queue names
     *
     * @return String[]
     */
    private static String[] getQueueNames() {
        // This is a comma separated string
        String queueNamesArg = System.getProperty("queueNames");
        if (queueNamesArg == null) {
            queueNamesArg = "sf_deferred_events";
        }

        return queueNamesArg.split(",");
    }

    /**
     * Get the number of threads from System properties.
     *
     * @return int
     */
    private static int getNumberOfThreads() {
        String param = System.getProperty("threads");
        if (param == null) {
            //default
            param = "3";
        }

        int threads;
        try {
            threads = Integer.parseInt(param);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The number of threads must me an integer");

        }

        if (threads < 1) {
            throw new IllegalArgumentException("You must specify a number of threads that is greather than 0");
        }

        return threads;
    }

    /**
     * Get a queue client from the system properties.
     *
     * @return QueueClient
     */
    private static QueueClient getQueue(String queueName) {
        String param = System.getProperty("messageQueue");
        if (param == null) {
            //default
            param = "rabbitmq";
        }

        String host = System.getProperty("messageQueueHost");
        if (host == null) {
            //default
            host = "localhost";
        }

        String portString = System.getProperty("messageQueuePort");
        Integer port;
        try {
            port = Integer.parseInt(portString);
        }
        catch (NumberFormatException e) {
            port = null;
        }

        if (param.equalsIgnoreCase("rabbitmq")) {
            if (port == null) {
                //default for rabbitmq
                port = 5672;
            }
            logger.info("Using rabbitmq at {}:{}", host, port);
            return new RabbitMqClient(host, port, queueName, new MessageConsumer(getExecutor()));
        }

        throw new IllegalArgumentException("Could not find QueueClient implementation named " + param);
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

        if (param.equalsIgnoreCase("http")) {
            return new HttpExecutor();
        }

        throw new IllegalArgumentException("Could not find ExecutorInterface implementation named " + param);
    }
}
