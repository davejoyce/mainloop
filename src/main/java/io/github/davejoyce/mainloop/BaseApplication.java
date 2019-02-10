package io.github.davejoyce.mainloop;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.requireNonNull;

public class BaseApplication implements Application {

    protected final Logger logger;

    private final Thread mainThread;
    private final Lock lock;
    private final Condition shuttingDown;

    @Parameter(description = "signal to trigger application exit",
               names = {"-s", "--signal"})
    private String exitSignal = "INT";

    private volatile boolean running;

    public static void main(String[] args) {
        BaseApplication application = new BaseApplication();
        JCommander jc = JCommander.newBuilder()
                                  .programName(application.programName())
                                  .addObject(application)
                                  .build();
        try {
            jc.parse(args);
        } catch (ParameterException pe) {
            System.err.println("Failed to process application parameter: " + pe.getMessage());
            pe.getJCommander().usage();
            System.exit(1);
        }

        ExitSignalHandler.install(application.exitSignal, application);
        application.run();
    }

    public BaseApplication() {
        this(Thread.currentThread());
    }

    public BaseApplication(Thread mainThread) {
        this.mainThread = requireNonNull(mainThread, "Main thread argument cannot be null");
        this.running = true;
        this.lock = new ReentrantLock();
        this.shuttingDown = lock.newCondition();

        this.logger = LoggerFactory.getLogger(getClass());
    }

    public String programName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public void run() {
        lock.lock();
        try {
            start();
            shuttingDown.await();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupted; shutting down...");
        } finally {
            lock.unlock();
        }
    }

    public void start() {
        logger.info("Starting {}...", programName());
    }

    public void stop() {
        logger.info("Stopping {}...", programName());
        running = false;
        this.notify();
    }

    private static Application getApplicationClass(String className) {
        try {
            return Class.forName(className).asSubclass(Application.class).newInstance();
        } catch (NullPointerException | ReflectiveOperationException e) {
            return new BaseApplication();
        }
    }

}
