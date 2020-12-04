package io.github.davejoyce.mainloop;

/**
 * Library constants.
 */
public final class Constants {

    /**
     * JVM system property which specifies the MainLoop implementation to instantiate.
     */
    public static final String PROPERTY_MAINLOOP_CLASS = "mainloop.class";

    /**
     * Default MainLoop implementation class.
     */
    public static final String DEFAULT_MAINLOOP_CLASS = "io.github.davejoyce.mainloop.SunMainLoop";

    private Constants() {}

}
