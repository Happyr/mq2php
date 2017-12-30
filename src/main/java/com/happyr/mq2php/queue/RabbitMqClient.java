package com.happyr.mq2php.queue;

import com.happyr.mq2php.MessageConsumer;
import com.happyr.mq2php.exception.MessageExecutionFailedException;
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
    protected MessageConsumer messageConsumer;

    public RabbitMqClient(String queueName, MessageConsumer mc) {
        messageConsumer = mc;
        this.queueName = queueName;
        errorQueueName = queueName + "_errors";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(queueName, true, false, false, null);
            channel.basicQos(1);

            consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueName, false, consumer);
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

    public void receive() {
        QueueingConsumer.Delivery delivery;
        try {
            delivery = consumer.nextDelivery();
        } catch (InterruptedException e) {
            return;
        }

        // De-cypher the message
        Message message = Marshaller.valueOf(delivery.getBody(), Message.class);
        message.setHeader("queue_name", queueName);

        try {
            // Handle the message
            messageConsumer.handle(message);
        } catch (MessageExecutionFailedException e) {
            reportError(e.getMessage());
        }

        try {
            // Ack the message when it has been handled
            consumer.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean reportError(String message) {

        try {
            channel.queueDeclare(errorQueueName, false, false, false, null);
            channel.basicPublish("", errorQueueName, null, message.getBytes());
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
