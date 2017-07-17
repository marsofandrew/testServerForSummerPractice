package spbspu.icc.ics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class FileController {

    public FileController() {
        clients = new Vector<>(10, 2);
        localFiles = new Vector<>(10, 2);
    }

    public FileController(FileController other) {
        this.clients = other.clients;
        this.localFiles = other.localFiles;
    }

    public synchronized String getCommand(String client) throws IOException {
      /*  StringBuilder it = new StringBuilder(clients.get(clients.indexOf(client)));
        //System.out.println(it);
        if (it.toString().equals(clients.lastElement())){
            clients.add(client);
            Scanner scanner = null;
            FileInputStream input = null;
            try{
                input  = new FileInputStream(client + ".mpr");
                scanner = new Scanner(input);
            }catch (FileNotFoundException e){      ////IOException and FileNotFoundException???
                e.printStackTrace();
            }
            localFiles.add(input);
            it = new StringBuilder(clients.get(clients.indexOf(client)));
        }
        String toReturn = "";
        boolean hasFileEnded = false;
        ///////////////////////////////////////////////////////// trash
        int distance1 = clients.indexOf(clients.firstElement());
        int distance2 = clients.indexOf(client);
        int distance = Math.abs(distance1-distance2);  //!!!!!!!!!!!!! help
        if(localFiles.elementAt(distance).read() == -1){
            System.out.println("One of the files has ended!");
            hasFileEnded = true;
        }
        if(!hasFileEnded){
            while(localFiles.elementAt(distance).read() != -1){    //// rly FileInputStream?
                toReturn += localFiles.elementAt(distance).read(); //// vs getline??
            }
            return toReturn;
        }
        else {
            hasFileEnded = false;
            return toReturn;
        }
        */
        String toReturn = null;
        File file = new File(client + ".mpr");
        toReturn = file.toString();

        return toReturn;
    }

    private Vector<String> clients;
    private Vector<File> localFiles;
    //Lock mutex;
}

