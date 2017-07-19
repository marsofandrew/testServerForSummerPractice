package spbspu.icc.ics.server;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class FileController {
    private Vector<String> clients;
    private Vector<Scanner> localFiles;
    private File[] mprList;
    public FileController() {
        // TODO: 19.07.2017 use mprList how client list 
        clients = new Vector<>(10, 2);
        localFiles = new Vector<>(10, 2);
        FileFilter mprFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.canRead() && pathname.getName().endsWith(".mpr");
            }
        };
        File mprDir = new File("Scenarios/");
        mprList = mprDir.listFiles(mprFileFilter);
       System.out.println("amount of files = " + mprList.length);
    }

    public FileController(FileController other) {
        this.clients = other.clients;
        this.localFiles = other.localFiles;
    }

    public synchronized String getCommand(String client) throws IOException {
        clients.add(client);
        StringBuilder it = new StringBuilder(clients.get(clients.indexOf(client)));
        if (it.toString().equals(clients.lastElement())){
            Scanner scanner = null;
            FileInputStream input = null;
            try{
                input  = new FileInputStream("Scenarios/"+client + ".mpr");
                scanner = new Scanner(input);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            localFiles.add(scanner);
            it = new StringBuilder(clients.get(clients.indexOf(client)));
        }
        String toReturn;
        boolean hasFileEnded = false;
        int distance1 = clients.indexOf(clients.firstElement());
        int distance2 = clients.indexOf(client);
        int distance = Math.abs(distance1-distance2);
        if(!localFiles.elementAt(distance).hasNext()){
            System.out.println("One of the files has ended!");
            hasFileEnded = true;
        }
        if(!hasFileEnded){
            toReturn = localFiles.elementAt(distance).nextLine();
            return toReturn;
        }
        else {
            //hasFileEnded = false;
            return "";
        }
    }

    public int getClientsAmount(){
        return mprList.length;
    }
}

