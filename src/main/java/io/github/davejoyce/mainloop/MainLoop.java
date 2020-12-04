package io.github.davejoyce.mainloop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static io.github.davejoyce.mainloop.Constants.DEFAULT_MAINLOOP_CLASS;
import static io.github.davejoyce.mainloop.Constants.PROPERTY_MAINLOOP_CLASS;

/**
 * Abstract superclass of objects which perform the "main loop" function in a
 * daemon program which must start and continue running.
 */
public abstract class MainLoop implements Runnable {

    protected Logger logger;

    private String programName = null;
    private boolean running = true;
    private OnReload onReload = null;

    /**
     * Create a MainLoop instance for this program JVM.
     *
     * @return the program MainLoop object (unstarted)
     */
    public static MainLoop newInstance() {
        String mainLoopClass = System.getProperty(PROPERTY_MAINLOOP_CLASS, DEFAULT_MAINLOOP_CLASS);
        try {
            return Class.forName(mainLoopClass).asSubclass(MainLoop.class).newInstance();
        } catch (ReflectiveOperationException roe) {
            throw new TypeNotPresentException(mainLoopClass, roe);
        }
    }

    public MainLoop forProgram(String programName) {
        this.programName = programName;
        return this;
    }

    public MainLoop onReload(OnReload onReload) {
        this.onReload = onReload;
        return this;
    }

    public void reload() {
        Optional.of(onReload).ifPresent(reloadHandler -> {
            logger.info("Reloading {}...", programName());
            reloadHandler.reload();
        });
    }

    public void run() {
        logger = LoggerFactory.getLogger(programName());
        start();
        synchronized (this) {
            while (this.running) {
                try {
                    this.wait();
                } catch (InterruptedException ie) {
                    // Log but otherwise ignore interrupt
                    logger.warn("Caught InterruptedException; continuing...");
                }
            }
            logger.info("Stopped");
        }
    }

    public void stop() {
        synchronized (this) {
            logger.info("Stopping {}...", programName());
            this.running = false;
            this.notify();
        }
    }

    protected MainLoop() {
        logger = LoggerFactory.getLogger(getClass());
    }

    protected String programName() {
        return Optional.ofNullable(programName)
                       .orElse(this.getClass().getSimpleName().toLowerCase().replaceAll("[_$]", "-"));
    }

    protected abstract void start();

}
