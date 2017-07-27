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

    private int[] ammounOfDetail, details;

    private File[] mprList;

    public FileController() {
        // TODO: 19.07.2017 use mprList how client list
        FileFilter mprFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.canRead() && pathname.getName().endsWith(".mpr");
            }
        };

        File mprDir = new File("Scenarios/");
        mprList = mprDir.listFiles(mprFileFilter);

        System.out.println("amount of files = " + mprList.length);
        clients = new Vector<>(mprList.length, 2);
        scaners = new Vector<>(mprList.length, 2);
        commands = new Vector<>(mprList.length, 2);

        ammounOfDetail = new int[mprList.length];
        details = new int[mprList.length];

        setAmmounOfDetail();
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
        if (details[index]<ammounOfDetail[index]) {
            command = scn.hasNext() ? scn.nextLine() : "File " + client + " has ended";
            if (!scn.hasNext()) {
                scn.close();
                if (++details[index] < ammounOfDetail[index]) {
                    command = "detail has done";
                    try {
                        scaners.set(index, new Scanner(mprList[index]));
                    } catch (FileNotFoundException e) {
                        System.err.println("General Error at File Controller");
                        e.printStackTrace();
                    }
                }
            }
        } else {
            command = "File " + client + " has ended";
        }

        commands.set(index, command);
        return command;
    }

    public int getClientsAmount() {
        return clients.size();
    }

    public synchronized String getPrevCommand(String client) throws InvalidArgumentException {
        client = client + ".mpr";
        if (clients.indexOf(client) == -1) {
            throw new InvalidArgumentException(new String[]{"Client don't found"});
        }
        return commands.get(clients.indexOf(client));
    }


    private void setAmmounOfDetail() {
        for (int i = 0; i < mprList.length; i++) {
            ammounOfDetail[i] = 3;
        }
    }
}

