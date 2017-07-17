package spbspu.icc.ics;
/**
 * Created by Андрей on 11.07.2017.
 */
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;
import org.zeromq.ZMQ.Poller;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Random;

//
//Asynchronous client-to-server (DEALER to ROUTER)
//
//While this example runs in a single process, that is just to make
//it easier to start and stop the example. Each task has its own
//context and conceptually acts as a separate process.

public class Main
{

    public static void main(String[] args) throws Exception {
        ZContext ctx = new ZContext();
        new Thread(new ClientTask("1")).start();
        new Thread(new ClientTask("2")).start();
        new Thread(new ClientTask("3")).start();
        new Thread(new ServerTask()).start();

        //  Run for 5 seconds then quit
        Thread.sleep(5 * 1000);
        ctx.destroy();
    }
}