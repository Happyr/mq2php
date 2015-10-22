package com.happyr.mq2php.queue;

import com.happyr.mq2php.Message;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * A Rabbit MQ client
 *
 * @author Tobias Nyholm
 */
public class RabbitMq implements QueueInterface {

    protected Connection connection;
    protected Channel channel;
    protected QueueingConsumer consumer;
    protected String queueName;
    protected String errorQueueName;

    public RabbitMq(String queueName) {
        this.queueName = queueName;
        errorQueueName = queueName + "_errors";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(queueName, true, false, false, null);

            consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueName, true, consumer);
        } catch (TimeoutException e) {
            throw new RuntimeException(e.getMessage());
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

    public Message receive() {
        Message message;
        try {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            message = new Message(new String(delivery.getBody()));
        } catch (InterruptedException e) {
            return null;
        }

        message.addHeader("queue_name", queueName);
        return message;
    }

    public boolean reportError(String message) {

        try {
            channel.queueDeclare(errorQueueName, false, false, false, null);
            channel.basicPublish("", errorQueueName, null, message.getBytes());
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
