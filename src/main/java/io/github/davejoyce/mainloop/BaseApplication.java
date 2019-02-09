package io.github.davejoyce.mainloop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

public class BaseApplication implements Runnable {

    protected final Logger logger;

    private final Thread mainThread;

    public BaseApplication() {
        this(Thread.currentThread());
    }

    public BaseApplication(Thread mainThread) {
        this.mainThread = requireNonNull(mainThread, "Main thread argument cannot be null");
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public void run() {
        while (!mainThread.isInterrupted()) {

        }
    }

}
