# Changelog


## From 0.2.* to 0.3.0

* The `console_path` header was removed.
* The tight coupling to fervo FervoDeferredEventBundle was also removed
* The `ShellExecutor` will now dispatch to the `dispatch_path` (same as the FastCgiExecutor`
* The data dispatched will be Base64 encoded. Once decoded you will get the complete message with headers
* New `queue` header will be available on the message once reached the dispatcher.
* The `QueueInterface::receive` must now return a `Message`.
* New command line argument `queueNames` to enable you to listen to more than one queue.