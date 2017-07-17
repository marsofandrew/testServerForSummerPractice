package spbspu.icc.ics;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Random;

/**
 * Created by Андрей on 12.07.2017.
 */
class ClientTask implements Runnable {
    private String identity;

    public ClientTask(String identity) {
        this.identity = identity;
    }

    public void run() {
        ZContext ctx = new ZContext();
        ZMQ.Socket client = ctx.createSocket(ZMQ.DEALER);

        //  Set random identity to make tracing easier
        String identity = "identity" + this.identity;
        client.setIdentity(identity.getBytes());
        client.connect("tcp://localhost:5570");

        ZMQ.PollItem[] items = new ZMQ.PollItem[]{new ZMQ.PollItem(client, ZMQ.Poller.POLLIN)};
        Selector selector = null;
        int requestNbr = 0;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            System.out.println("Some Exception in Selector" + e);
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep( 1 + new Random().nextInt(10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //  Tick once per second, pulling in arriving messages
            for (int centitick = 0; centitick < 100; centitick++) {

                ZMQ.poll(selector, items, 10);


                if (items[0].isReadable()) {
                    ZMsg msg = ZMsg.recvMsg(client);
                    msg.getLast().print(identity);
                    msg.destroy();
                }
            }
            client.send(String.format("Change request #%d", ++requestNbr), 0);
        }
        ctx.destroy();
    }
}
