# Deferred Event Java Worker

This is a worker for [FervoDeferredEventBundle][fervoSource]. It is written in Java. The purpose of
this application is to pull messages from a message queue and initiate php to execute the job in that message.


## Configuration

There is some configuration you might want to consider when starting the worker.

### executor

```java -Dexecutor=http -jar DeferredEventJavaWorker.jar```

Possible values are:

 * http
 * shell

### messageQueue

```java -DmessageQueue=rabbitmq -jar DeferredEventJavaWorker.jar```

Possible values are:

 * rabbitmq

## Message headers

The message should contain some headers to tell the worker what is should do. The message and the header look a lot
like the HTTP protocol. This is an example message:

```batch
php_bin: /usr/local/bin/php
console_path: /Users/tobias/Workspace/Symfony/app/console
dispatch_path: /Users/tobias/Workspace/Symfony/app/../bin/dispatch.php
fastcgi_host: localhost
fastcgi_port: 9000

TzozOToiU3ltZm9ueVxDb21wb25lbnRcRXZlbnREm9ueVxDb21wb25lbnRcRXZlbnREaXNwYXRjRXZlbnREm9ueVxDb21wb25lbnRcRXZlbnREaXNwYXRjRXZlbnREm9ueVxDb21wb25lbnRcRXZlbnREaXNwYXRjaGVyXEV2ZW50AG5hbWUiO3M6MTA6ImZvby5hY3Rpb24iO30=
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
