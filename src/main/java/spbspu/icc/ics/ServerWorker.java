package spbspu.icc.ics;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.Random;

/**
 * Created by Андрей on 12.07.2017.
 */
class ServerWorker implements Runnable{
    private ZContext ctx;
    private static Random rand = new Random(System.nanoTime());
    public ServerWorker(ZContext ctx) {
        this.ctx = ctx;
    }

    public void run() {
        ZMQ.Socket worker = ctx.createSocket(ZMQ.DEALER);
        worker.connect("inproc://backend");

        while (!Thread.currentThread().isInterrupted()) {
            //  The DEALER socket gives us the address envelope and message
            ZMsg msg = ZMsg.recvMsg(worker);
            msg.getLast().print(" Server Worker  ");
            ZFrame address = msg.pop();
            ZFrame content = msg.pop();


            assert (content != null);
            msg.destroy();

            //  Send 0..4 replies back
            int replies = rand.nextInt(5);
            for (int reply = 0; reply < replies; reply++) {
                //  Sleep for some fraction of a second
                try {
                    Thread.sleep(rand.nextInt(1000) + 1);
                } catch (InterruptedException e) {
                }
                address.send(worker, ZFrame.REUSE + ZFrame.MORE);

                content.send(worker, ZFrame.REUSE);


            }

            address.destroy();
            content.destroy();
        }
        ctx.destroy();
    }
}
