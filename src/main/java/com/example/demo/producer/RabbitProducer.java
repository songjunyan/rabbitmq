package com.example.demo.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author sls
 * @date 2018/1/19
 */
public class RabbitProducer {
    private static final String EXCHANGE_NAME = "exchange_demo";
    private static final String ROUTING_KEY = "routingkey_demo";
    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "127.0.0.1";
    //rabbitmq默认端口5672
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("root");
        factory.setPassword("root");

        //创建连接
        Connection connection = factory.newConnection();
        //创建通道
        Channel channel = connection.createChannel();
        //创建一个type="direct"、持久化的、非自动删除的--------交换器
        /**
         * Declare an exchange.
         * @see com.rabbitmq.client.AMQP.Exchange.Declare
         * @see com.rabbitmq.client.AMQP.Exchange.DeclareOk
         * @param exchange the name of the exchange
         * @param type the exchange type
         * @param durable true if we are declaring a durable exchange (the exchange will survive a server restart)
         * @param autoDelete true if the server should delete the exchange when it is no longer in use
         * @param arguments other properties (construction arguments) for the exchange
         * @return a declaration-confirm method to indicate the exchange was successfully declared
         * @throws java.io.IOException if an error is encountered
         */
        channel.exchangeDeclare(EXCHANGE_NAME,"direct",true,false,null);
        //创建一个持久化、非排他的、非自动删除的--------------队列
        /**
         * Declare a queue
         * @see com.rabbitmq.client.AMQP.Queue.Declare
         * @see com.rabbitmq.client.AMQP.Queue.DeclareOk
         * @param queue the name of the queue
         * @param durable true if we are declaring a durable queue (the queue will survive a server restart)
         * @param exclusive true if we are declaring an exclusive queue (restricted to this connection)
         * @param autoDelete true if we are declaring an autodelete queue (server will delete it when no longer in use)
         * @param arguments other properties (construction arguments) for the queue
         * @return a declaration-confirm method to indicate the queue was successfully declared
         * @throws java.io.IOException if an error is encountered
         */
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        //将交换器与队列通过路由键绑定
        /**
         * Bind a queue to an exchange, with no extra arguments.
         * @see com.rabbitmq.client.AMQP.Queue.Bind
         * @see com.rabbitmq.client.AMQP.Queue.BindOk
         * @param queue the name of the queue
         * @param exchange the name of the exchange
         * @param routingKey the routing key to use for the binding
         * @return a binding-confirm method if the binding was successfully created
         * @throws java.io.IOException if an error is encountered
         */
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);
        for(int i=0;i<10000000;i++) {
            //发送一条持久化的消息：hello world!
            String message = "hello world!"+i;
            /**
             * Publish a message.
             *
             * Publishing to a non-existent exchange will result in a channel-level
             * protocol exception, which closes the channel.
             *
             * Invocations of <code>Channel#basicPublish</code> will eventually block if a
             * <a href="http://www.rabbitmq.com/alarms.html">resource-driven alarm</a> is in effect.
             *
             * @see com.rabbitmq.client.AMQP.Basic.Publish
             * @see <a href="http://www.rabbitmq.com/alarms.html">Resource-driven alarms</a>
             * @param exchange the exchange to publish the message to
             * @param routingKey the routing key
             * @param props other properties for the message - routing headers etc
             * @param body the message body
             * @throws java.io.IOException if an error is encountered
             */
            /**MessageProperties.PERSISTENT_TEXT_PLAIN Content-type "text/plain", deliveryMode 2 (persistent), priority zero */
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        }
        //关闭资源
        channel.close();
        connection.close();

    }
}
