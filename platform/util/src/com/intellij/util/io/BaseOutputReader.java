/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.util.io;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author traff
 */
public abstract class BaseOutputReader {
  private static final Logger LOG = Logger.getInstance("#com.intellij.util.io.BaseOutputReader");

  protected final Reader myReader;
  protected volatile boolean isStopped = false;

  private final char[] myBuffer = new char[8192];
  private boolean skipLF = false;

  private Future<?> myFinishedFuture = null;

  public BaseOutputReader(@NotNull Reader reader) {
    myReader = reader;
  }

  protected void start() {
    if (myFinishedFuture == null) {
      myFinishedFuture = executeOnPooledThread(new Runnable() {
        public void run() {
          doRun();
        }
      });
    }
  }

  protected abstract Future<?> executeOnPooledThread(Runnable runnable);

  protected void doRun() {
    try {
      while (true) {
        boolean read = readAvailable();

        if (isStopped) {
          myReader.close();
          break;
        }

        Thread.sleep(read ? 1 : 5); // give other threads a chance
      }
    }
    catch (InterruptedException ignore) {
    }
    catch (IOException e) {
      LOG.info(e);
    }
    catch (Exception e) {
      LOG.error(e);
    }
  }

  protected synchronized boolean readAvailable() throws IOException {
    char[] buffer = myBuffer;
    StringBuilder token = new StringBuilder();

    boolean read = false;
    while (myReader.ready()) {
      int n = myReader.read(buffer);
      if (n <= 0) break;
      read = true;

      for (int i = 0; i < n; i++) {
        char c = buffer[i];
        if (skipLF && c != '\n') {
          token.append('\r');
        }

        if (c == '\r') {
          skipLF = true;
        }
        else {
          skipLF = false;
          token.append(c);
        }

        if (c == '\n') {
          onTextAvailable(token.toString());
          token.setLength(0);
        }
      }
    }

    if (token.length() != 0) {
      onTextAvailable(token.toString());
      token.setLength(0);
    }
    return read;
  }

  protected abstract void onTextAvailable(@NotNull String text);

  public void stop() {
    isStopped = true;
  }

  public void waitFor() throws InterruptedException {
    try {
      myFinishedFuture.get();
    }
    catch (ExecutionException e) {
      LOG.error(e);
    }
  }
}
