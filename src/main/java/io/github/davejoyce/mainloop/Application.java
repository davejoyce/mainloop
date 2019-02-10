package io.github.davejoyce.mainloop;

public interface Application extends Runnable {

    String programName();

    void start();

    void stop();

}
