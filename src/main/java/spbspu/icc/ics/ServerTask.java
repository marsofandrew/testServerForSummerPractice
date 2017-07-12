package spbspu.icc.ics;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * Created by Андрей on 12.07.2017.
 */
class ServerTask implements Runnable {
    public void run() {
        ZContext ctx = new ZContext();

        //  Frontend socket talks to clients over TCP
        ZMQ.Socket frontend = ctx.createSocket(ZMQ.ROUTER);
        frontend.bind("tcp://*:5570");

        //  Backend socket talks to workers over inproc
        ZMQ.Socket backend = ctx.createSocket(ZMQ.DEALER);
        backend.bind("inproc://backend");


        //  Launch pool of worker threads, precise number is not critical
        for (int threadNbr = 0; threadNbr < 5; threadNbr++)
            new Thread(new ServerWorker(ctx)).start();

        //  Connect backend to frontend via a proxy
        ZMQ.proxy(frontend, backend, null);

        ctx.destroy();
    }
}
