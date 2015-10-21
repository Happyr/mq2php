package com.happyr.mq2php.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;

/**
 * A Rabbit MQ client
 *
 * @author Tobias Nyholm
 */
public class RabbitMq implements QueueInterface {

    private final static String QUEUE_NAME = "sf_deferred_events";
    private final static String ERROR_QUEUE_NAME = "sf_deferred_events_errors";

    protected Connection connection;
    protected Channel channel;
    protected QueueingConsumer consumer;

    public RabbitMq() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            consumer = new QueueingConsumer(channel);
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        channel.close();
        connection.close();

        super.finalize();
    }

    @Override
    public String receive() {

        String message;
        try {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            message = new String(delivery.getBody());
        } catch (InterruptedException e) {
            message = "";
        }

        return message;
    }

    @Override
    public boolean reportError(String message) {

        try {
            channel.queueDeclare(ERROR_QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", ERROR_QUEUE_NAME, null, message.getBytes());
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
