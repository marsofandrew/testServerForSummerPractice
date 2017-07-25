package spbspu.icc.ics.server;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

class FileController {
    private Vector<Scanner> scaners;
    private Vector<String> clients;
    private Vector<String> commands;


    public FileController() {
        // TODO: 19.07.2017 use mprList how client list
        FileFilter mprFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.canRead() && pathname.getName().endsWith(".mpr");
            }
        };
        File[] mprList;
        File mprDir = new File("Scenarios/");
        mprList = mprDir.listFiles(mprFileFilter);

        System.out.println("amount of files = " + mprList.length);
        clients = new Vector<>(mprList.length, 2);
        scaners = new Vector<>(mprList.length, 2);
        commands = new Vector<>(mprList.length,2);
        for (File file : mprList) {
            clients.add(file.getName());
            commands.add("No command");
            try {
                scaners.add(new Scanner(file));
            } catch (FileNotFoundException e) {
                System.err.println("Файл " + file + "не лежит в папке Scenarios");
                e.printStackTrace();
                System.exit(2);
            }
        }
    }

    public synchronized String getCommand(String client) throws InvalidArgumentException {
        client = client + ".mpr";
        String command = "";
        int index = clients.indexOf(client);
        if (index == -1) {
            throw new InvalidArgumentException(new String[]{"Client don't found"});
        }

        Scanner scn = scaners.get(index);
        command = scn.hasNext() ? scn.nextLine() : "File " + client + " has ended";
        /*if(!scn.hasNext()){
          //close Scanner and reopen it.
            scn.close();
            scaners.set(index,new Scanner(new File()))
        }
        */
        commands.set(index,command);
        return command;
    }

    public int getClientsAmount() {
        return clients.size();
    }
    public synchronized String getPrevCommand(String client) throws InvalidArgumentException {
        client = client + ".mpr";
        if (clients.indexOf(client)==-1){
            throw new InvalidArgumentException(new String[]{"Client don't found"});
        }
        return commands.get(clients.indexOf(client));
    }
}

