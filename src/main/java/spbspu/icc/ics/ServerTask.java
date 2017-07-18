package spbspu.icc.ics;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Андрей on 12.07.2017.
 */
class ServerTask implements Runnable {
    private int maxThread;

    public ServerTask() {
        maxThread = Runtime.getRuntime().availableProcessors(); // ammount of Threads equals ammount of proessor's
        // threads
        System.out.println("maxThread = " + maxThread);
    }

    public void run() {
        ZContext ctx = new ZContext();

        //  Frontend socket talks to clients over TCP
        ZMQ.Socket frontend = ctx.createSocket(ZMQ.ROUTER);
        frontend.bind("tcp://*:5570");

        //  Backend socket talks to workers over inproc
        ZMQ.Socket backend = ctx.createSocket(ZMQ.DEALER);
        backend.bind("inproc://backend");


        //  Launch pool of worker threads, precise number is not critical
        for (int threadNbr = 0; threadNbr < maxThread; threadNbr++) {

            new Thread(new ServerWorker(ctx, ZMQ.DEALER, new FileController())).start();
        }
        //  Connect backend to frontend via a proxy
        ZMQ.proxy(frontend, backend, null);

        ctx.destroy();

    }

    public void setMaxThread(int value) throws InvalidArgumentException {
        if (value > 0) {
            maxThread = value;
        } else {
            System.err.println("You can't set value <=0");
            // throw new InvalidArgumentException();
        }
    }

}
