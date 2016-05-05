# Changelog

## From 0.4.0 to master


## From 0.3.1 to 0.4.0

* Fixed memory leaks
* New message syntax, we do not longer use HTTP-like messages. We use a Json syntax instead.
* We do no longer accept any arguments when running the application. We only use system properties.
* Added logging to a log file.

## From 0.3.0 to 0.3.1

* The data dispached will be Base64 **and** URL encoded. This will fix the bug where PHP interpets a Base64 endcoded string as two strings becuase it contains a plus sign (+). 

## From 0.2.* to 0.3.0

* The `console_path` header was removed.
* The tight coupling to fervo FervoDeferredEventBundle was also removed
* The `ShellExecutor` will now dispatch to the `dispatch_path` (same as the FastCgiExecutor)
* The data dispatched will be Base64 encoded. Once decoded you will get the complete message with headers. *This is a huge BC break!*
* New `queue` header will be available on the message once reached the dispatcher.
* The `QueueInterface::receive` must now return a `Message`.
* New command line argument `queueNames` to enable you to listen to more than one queue.
