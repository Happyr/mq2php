# Deferred Event Java Worker

This is a worker for [FervoDeferredEventBundle][fervoSource]. It is written in Java. The purpose of
this application is to pull messages from a message queue and initiate php to execute the job in that message.

## Installation

Download the jar file and put it somewhere like /opt/DeferredEventJavaWorker/DeferredEventJavaWorker.jar. You might
want to use a init script. See [this file][initFile] for a template.

You do also need to install a message queue like [Rabbit MQ][http://www.rabbitmq.com/]. When you got everything
set up for the worker you need to install and configure [FervoDeferredEventBundle][fervoSource]. Use the **amqp backend.**

Remember to protect your queue. If someone unauthorized could write to your queue he will get the same permission to execute
programs as PHP does. Make sure you restrict access on the queue to localhost or make sure you know what you are doing.

## Configuration

There is some configuration you might want to consider when starting the worker.

### executor

How do you want to execute the php job? Do you want to do it with PHP-FPM (fastcgi) or PHP cli (shell command).

```java -Dexecutor=http -jar DeferredEventJavaWorker.jar```

Possible values are:

 * http
 * shell

### messageQueue

What message queue system do you want to use?

```java -DmessageQueue=rabbitmq -jar DeferredEventJavaWorker.jar```

Possible values are:

 * rabbitmq

When you are using rabbitmq we will connect to localhost with the official rabbit mq client library.

### Number of worker threads

As default there is 5 threads listening to the queue. These threads are waiting for a response from the PHP script. If
you are planning to have several long running script simultaneously you may want to increase this. Usually you don't need
to bother.

```java -jar DeferredEventJavaWorker.jar 5```

## Message headers

The message should contain some headers to tell the worker what is should do. The message and the header look a lot
like the HTTP protocol. This is an example message:

```batch
php_bin: /usr/local/bin/php
console_path: /Users/tobias/Workspace/Symfony/app/console
dispatch_path: /Users/tobias/Workspace/Symfony/app/../bin/dispatch.php
fastcgi_host: localhost
fastcgi_port: 9000

TzozOToiU3ltZm9ueVxDb21wb25lbnRcRXZlbnREm9ueVxDb21wb25lbnRcRXZlbnREaXNwYXRjRXZlbnREm9ueVxDb21wb25lbnRcRXZlbnRE
```

### Shell Executor

These headers must exist when you are using the Shell executor.

#### php_bin

This should be a path to the php executable.

#### console_path

This is the path to the Symfony app/console.

### PHP-FPM (fastcgi)

These headers must exist when you are using the HTTP executor.

#### fastcgi_host

If you are using PHP-FPM you have to specify a host to connect to. This is normally "localhost".

#### fastcgi_port

The port that we should use together with **fastcgi_host**.

#### dispatch_path

The absolute path to the dispatch.php. This is normally /path/to/symfony/bin/dispatch.php


[fervoSource]: https://github.com/fervo/FervoDeferredEventBundle
[initFile]: https://github.com/HappyR/DeferredEventJavaWorker/blob/master/deferred-event-java-worker.init-file