package org.jetbrains.jps.api;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eugene Zhuravlev
 *         Date: 9/24/11
 */
public class SequentialTaskExecutor implements Executor {
  private final Executor myExecutor;
  private final Queue<FutureTask> myTaskQueue = new LinkedBlockingQueue<FutureTask>();
  private final AtomicBoolean myInProgress = new AtomicBoolean(false);
  private final Runnable USER_TASK_RUNNER = new Runnable() {
    public void run() {
      final FutureTask task = myTaskQueue.poll();
      try {
        if (task != null && !task.isCancelled()) {
          task.run();
        }
      }
      finally {
        myInProgress.set(false);
        if (!myTaskQueue.isEmpty()) {
          processQueue();
        }
      }
    }
  };

  public SequentialTaskExecutor(Executor executor) {
    myExecutor = executor;
  }

  @Override
  public void execute(Runnable task) {
    submit(task);
  }

  public RunnableFuture submit(Runnable task) {
    final FutureTask futureTask = new FutureTask(task, null);
    if (myTaskQueue.offer(futureTask)) {
      processQueue();
    }
    else {
      throw new RuntimeException("Failed to queue task: " + task);
    }
    return futureTask;
  }

  protected void processQueue() {
    if (!myInProgress.getAndSet(true)) {
      myExecutor.execute(USER_TASK_RUNNER);
    }
  }

}
