# Message queue 2 PHP

This Java application pull data from a message queue and give the message to PHP by using PHP-FPM. It could be used as a worker for [SimpleBus](https://github.com/SimpleBus) asynchronous messages.

## Installation

Download the jar file and put it somewhere like /opt/mq2php/mq2php.jar. You might
want to use a init script. See [this file][initFile] for a template. You do also need to install a message queue like [Rabbit MQ](http://www.rabbitmq.com/).

Remember to protect your queue. If someone unauthorized could write to your queue he will get the same permission to execute
programs as PHP does. Make sure you restrict access on the queue to localhost or make sure you know what you are doing.

## Configuration

There is some configuration you might want to consider when starting the worker.

### executor

How do you want to execute the php job? Do you want to do it with PHP-FPM (fastcgi) or PHP cli (shell) command. Use PHP-FPM for best performance.

```bash 
java -Dexecutor=fastcgi -jar mq2php.jar
```

Possible values are:

 * fastcgi
 * shell

### messageQueue

What message queue system do you want to use?

```bash 
java -DmessageQueue=rabbitmq -jar mq2php.jar
```

Possible values are:

 * rabbitmq

When you are using rabbitmq we will connect to localhost with the official rabbit mq client library.

### queueNames

You can subscribe to different queues with different names. You should separate names by a comma.

```bash
java -DmessageQueue=rabbitmq -DqueueNames=foo,bar,baz -jar mq2php.jar
```

These queues will be evenly distributed over the worker threads.

### threads

The number of threads you want to use for each queue. As default there ara 3 threads listening to each queue. These
threads are waiting for a response from the PHP script. If you are planning to have several long running script
simultaneously you may want to increase this. Usually you don't need to bother.

```bash 
java -jar mq2php.jar -Dthreads=3
```

## Message headers

The message should contain some headers to tell the worker what is should do. The message is a json object. This is an example message:

```js
{
   "headers":[
      {
         "key":"fastcgi_host",
         "value":"localhost"
      },
      {
         "key":"fastcgi_port",
         "value":9000
      },
      {
         "key":"dispatch_path",
         "value":"\/absolute\/path\/bin\/dispatch-message.php"
      },
      {
         "key":"php_bin",
         "value":"\/usr\/local\/Cellar\/php70\/7.0.4\/bin\/php"
      }
   ],
   "body":"this body is not of any interest for mq2php."
}
```

When the message is sent to PHP it will contain one extra header. It will be the `queue_name` header that contains the
name of the queue.


### Shell Executor

These headers must exist when you are using the Shell executor.

* php_bin - This should be a path to the php executable.
* dispatch_path - The absolute path to the dispatch.php. This is normally /path/to/symfony/bin/dispatch.php.

### PHP-FPM (fastcgi)

These headers must exist when you are using the FastCGI executor.

* fastcgi_host - If you are using PHP-FPM you have to specify a host to connect to. This is normally "localhost".
* fastcgi_port - The port that we should use together with **fastcgi_host**.
* dispatch_path - The absolute path to the dispatch.php. This is normally /path/to/symfony/bin/dispatch.php.

## Contribute

If you want to make changed and compile the application your self you can do so with:

```bash
mvn clean compile assembly:single
java -Dexecutor=shell -DqueueNames=asynchronous_commands,asynchronous_events -jar target/mq2php-0.4.0-SNAPSHOT-jar-with-dependencies.jar
```

[initFile]: https://github.com/Happyr/mq2php/blob/master/mq2php.init-file
