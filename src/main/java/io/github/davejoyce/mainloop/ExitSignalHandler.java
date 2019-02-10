package io.github.davejoyce.mainloop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import static java.util.Objects.requireNonNull;

class ExitSignalHandler implements SignalHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExitSignalHandler.class);

    private Application application;
    private SignalHandler oldHandler;

    public static ExitSignalHandler install(String signalName, Application application) {
        Signal exitSignal = new Signal(signalName);
        ExitSignalHandler exitSignalHandler = new ExitSignalHandler(application);
        exitSignalHandler.oldHandler = Signal.handle(exitSignal, exitSignalHandler);
        return exitSignalHandler;
    }

    @Override
    public void handle(Signal sig) {
        try {
            LOGGER.info("Exit signal handler called for signal: {}", sig);
            application.stop();

            // Chain back to previous handler, if one exists
            if (oldHandler != SIG_DFL && oldHandler != SIG_IGN) {
                oldHandler.handle(sig);
            }
        } catch (Exception e) {
            LOGGER.error("Signal handler failed; reason: ", e);
        }
    }

    private ExitSignalHandler(Application application) {
        this.application = requireNonNull(application, "Application argument cannot be null");
    }

}
