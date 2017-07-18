package spbspu.icc.ics;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FilenameUtils;

//import static spbstu.icc.ics.machinery.Main.machNumber;
//import static spbstu.icc.ics.machinery.Main.mprFileFilter;

class FileController {

    final String ERROR_NO_FILES_PRESENT = "No MPR files present";
    private final String ERROR_NULL_CLIENT = "Null client";
    private final String ERROR_WRONG_CLIENT = "Wrong client";

    private Lock lock;
    private File[] mprFileArray;
    private Scanner scanner;
    private String fileName;
    private String commandString;

    private boolean foundClient = false;
    private List<String> clientsArrayList;
    private Map<String, Scanner> scannerClientMap;

    FileController() {
        FileFilter mprFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.canRead() && pathname.getName().endsWith(".mpr");
            }
        };
        lock = new ReentrantLock();
        File mprDir = new File("./Scenarios/");
        mprFileArray = mprDir.listFiles(mprFileFilter);
        clientsArrayList = new ArrayList<>(4);
        scannerClientMap = new HashMap<>();
    }

    public synchronized String getCommand(String client) {
        lock.lock();
        if (mprFileArray == null) {
            lock.unlock();
            return ERROR_NO_FILES_PRESENT;
        }
        if (client != null) {
            if (client.isEmpty()) {
                System.err.println(ERROR_NULL_CLIENT);
                lock.unlock();
                return ERROR_NULL_CLIENT;
            }
            for (File mprListElem : mprFileArray) {
                fileName = FilenameUtils.removeExtension(mprListElem.getName());
                if (clientsArrayList.contains(fileName)) {
                    foundClient = true;
                    scanner = scannerClientMap.get(client);
                    if (scanner != null && scanner.hasNext()) {
                        commandString = scanner.nextLine();
                    } else {
                        commandString = "File " + fileName + " has ended";
                        System.out.println(commandString);
                    }
                } else {
                    if (client.equals(fileName)) {
                        foundClient = true;
                        clientsArrayList.add(client);
                        try {
                            scanner = new Scanner(mprListElem);
                        } catch (FileNotFoundException e) {
                            System.err.println(e.toString());
                            continue;
                        }
                        scannerClientMap.put(client, scanner);
                        break;
                    }
                }
            }
            if (!foundClient) {
                System.err.println("Client trying to connect is not configured properly: " + client);
                lock.unlock();
                return ERROR_WRONG_CLIENT;
            } else {
                System.out.println("Got command: " + commandString);
                lock.unlock();
                return commandString;
            }
        } else {
            System.out.println("Null client");
            return "NULL";
        }
    }

    void cleanUp() {
        clientsArrayList.clear();
        scannerClientMap.clear();
    }
}
