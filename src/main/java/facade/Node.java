package facade;

import persistence.MongoHandler;
import persistence.models.Transaction;
import service.BlockServices;
import service.NodeServices;

import java.util.Scanner;

public class Node {

    public Node(){
        start();
    }

    private void start(){
        boolean running = true;
        Transaction transaction = null;
        Scanner sc = new Scanner(System.in);
        MongoHandler handler = new MongoHandler();
        BlockServices.generateGenesisBlockIfDoesNotExist(handler);

        while(running){
            int command = NodeServices.getNextCommand(sc);

            switch (command){
                case 1 -> NodeServices.generateAndPrintKeyPair();
                case 2 -> NodeServices.registerEntityAtCA(sc, handler);
                case 3 -> transaction = NodeServices.createTransaction(sc, handler);
                case 4 -> transaction = BlockServices.mineBlock(transaction, handler);
                case 5 -> NodeServices.trace(sc, handler);
                case 6 -> NodeServices.traceAndDecrypt(sc, handler);
                case 7 -> running = NodeServices.gracefullyShutDown(sc,handler);
            }
        }
    }

    public static void main(String[] args) {
        new Node();
    }
}
