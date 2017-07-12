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
    //---------------------------------------------------------------------
    //This is our client task
    //It connects to the server, and then sends a request once per second
    //It collects responses as they arrive, and it prints them out. We will
    //run several client tasks in parallel, each with a different random ID.

    private static Random rand = new Random(System.nanoTime());



    //This is our server task.
    //It uses the multithreaded server model to deal requests out to a pool
    //of workers and route replies back to clients. One worker can handle
    //one request at a time but one client can talk to multiple workers at
    //once.



    //Each worker task works on one request at a time and sends a random number
    //of replies back, with random delays between replies:


    //The main thread simply starts several clients, and a server, and then
    //waits for the server to finish.

    public static void main(String[] args) throws Exception {
        ZContext ctx = new ZContext();
        new Thread(new ClientTask()).start();
        new Thread(new ClientTask()).start();
        new Thread(new ClientTask()).start();
        new Thread(new ServerTask()).start();

        //  Run for 5 seconds then quit
        Thread.sleep(5 * 1000);
        ctx.destroy();
    }
}