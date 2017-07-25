package spbspu.icc.ics;

import spbspu.icc.ics.server.ServerTask;

public class Main
{

    public static void main(String[] args) throws Exception {

        new Thread(new ClientTask("0D05-115C")).start();
        new Thread(new ClientTask("08AE-0D05")).start();
        new Thread(new ClientTask("115C-15B3")).start();
        //new Thread(new ServerTask()).start();

        //  Run for 5 seconds then quit
        Thread.sleep(5 * 1000);
    }
}