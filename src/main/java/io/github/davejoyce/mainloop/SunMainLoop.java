package io.github.davejoyce.mainloop;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Default implementation of {@code MainLoop}. This class uses undocumented Sun
 * Microsystems (Oracle) JRE internal facilities to register and handle OS
 * signals. Other JREs will likely require their own implementation class, which
 * can be specified to the program JVM at startup via the
 * {@link Constants#PROPERTY_MAINLOOP_CLASS} system property.
 */
public class SunMainLoop extends MainLoop {

    /**
     * Internal implementation of {@code SignalHandler} which
     * causes the program to terminate.
     */
    class Terminator implements SignalHandler {

        @Override
        public void handle(Signal sig) {
            stop();
        }
    }

    /**
     * Internal implementation of {@code SignalHandler} which
     * causes the program to reload.
     */
    class Reloader implements SignalHandler {

        @Override
        public void handle(Signal sig) {
            reload();
        }
    }

    @Override
    protected void start() {
        Terminator terminator = new Terminator();
        Reloader reloader = new Reloader();

        // Install handlers for main POSIX signals
        install("INT", terminator);
        install("TERM", terminator);
        install("HUP", reloader);
    }

    private SignalHandler install(String signalName, SignalHandler handler) {
        logger.trace("Installing handler for signal '{}'", signalName);
        return Signal.handle(new Signal(signalName), handler);
    }

}
