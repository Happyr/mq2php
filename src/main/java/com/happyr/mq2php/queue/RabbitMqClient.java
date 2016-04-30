package com.happyr.mq2php.queue;

import com.happyr.mq2php.message.Message;
import com.happyr.mq2php.util.Marshaller;
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
public class RabbitMqClient implements QueueClient {

    protected Connection connection;
    protected Channel channel;
    protected QueueingConsumer consumer;
    protected String queueName;
    protected String errorQueueName;

    public RabbitMqClient(String queueName) {
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
            message = Marshaller.valueOf(delivery.getBody(), Message.class);
        } catch (InterruptedException e) {
            return null;
        }

        message.setHeader("queue_name", queueName);
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
