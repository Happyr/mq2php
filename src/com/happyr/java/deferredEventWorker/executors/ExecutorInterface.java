package com.happyr.java.deferredEventWorker.executors;

import com.happyr.java.deferredEventWorker.Message;

/**
 * Created by tobias on 30/03/14.
 */
public interface ExecutorInterface {
    String execute(Message message);
}
