package spbspu.icc.ics;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.awt.*;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Андрей on 12.07.2017.
 */
class ServerWorker implements Runnable {
    private ZContext ctx;
    private String toSend;
    private FileController fileController;
    private ZMQ.Socket worker;


    public ServerWorker(ZContext ctx, int SocketType, FileController fileController) {
        this.ctx = ctx;
        worker = ctx.createSocket(SocketType);
        this.fileController = fileController;
    }

    public void run() {

        worker.connect("inproc://backend");

        while (!Thread.currentThread().isInterrupted()) {
            //  The DEALER socket gives us the address envelope and message
            ZMsg msg = ZMsg.recvMsg(worker);
            msg.getLast().print("Server get:");
            ZFrame address = msg.pop();

            String reply = address.toString();
            // msg.popString return Massege string;
            System.out.println("reply " + reply);
            if (!checkReply(msg.popString())) {
                System.err.println("something wrong 1 at SErverWorker");
            }
            try {
                toSend = fileController.getCommand(reply);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ZFrame forSend = ZMsg.newStringMsg(toSend).pop();


            msg.destroy();
            /*try {
                Thread.sleep(1 + new Random().nextInt(10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */
            address.send(worker, ZFrame.REUSE + ZFrame.MORE);
            forSend.send(worker, ZFrame.REUSE);

            address.destroy();

        }
        ctx.destroy();
        System.out.println("end ServerTask");
    }

    public String getToSend() {
        return toSend;
    }

    public void setToSend(String string) {
        toSend = string;
    }

    private boolean checkReply(String reply) {
        if (toSend == reply || toSend == null) {
            return true;
        }
        return false;
    }
}
