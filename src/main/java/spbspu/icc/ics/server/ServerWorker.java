package spbspu.icc.ics.server;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;
import spbspu.icc.ics.server.FileController;

import java.io.IOException;

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

            String identity = address.toString();
            //String reply =  msg.popString();
            System.out.println("reply's identity " + identity);
            msg.destroy();

            if (checkReply(msg.popString(),identity)) {
                try {
                    toSend = fileController.getCommand(identity);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
                ZFrame forSend = ZMsg.newStringMsg(toSend).pop();
                System.out.println("Server send to client " + identity + toSend);

                address.send(worker, ZFrame.REUSE + ZFrame.MORE);
                forSend.send(worker, ZFrame.REUSE);
                forSend.destroy();
            } else {
                System.out.println("Server Not true");
            }

            address.destroy();

        }
        ctx.destroy();
    }

    public String getToSend() {
        return toSend;
    }

    public void setToSend(String string) {
        toSend = string;
    }

    private boolean checkReply(String reply, String identity) {
        System.out.println(identity);
        System.out.println("reply == " + reply);
        try {
            if (reply.equals(fileController.getPrevCommand(identity))|| fileController.getPrevCommand(identity)=="No command") {
                return true;
            }
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }
}
