package io.github.davejoyce.mainloop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

public class SunMainLoopTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testReload() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        MainLoop mainLoop = MainLoop.newInstance()
                                    .forProgram("test")
                                    .onReload(() -> {
                                        latch.countDown();
                                        logger.debug("Reload handler called");
                                    });
        assertTrue(mainLoop instanceof SunMainLoop);

        mainLoop.start();
        signalMyself(1);
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(latch.getCount(), 0);

        mainLoop.stop();
    }

    private static int getPid() {
        String s =  ManagementFactory.getRuntimeMXBean().getName();
        return Integer.parseInt(s.substring(0, s.indexOf('@')));
    }

    private static void signalMyself(int signal) {
        try {
            Runtime.getRuntime().exec(String.format("kill -%d %d", signal, getPid()));
        } catch (IOException ie) {
            throw new Error(ie);
        }
    }

}
