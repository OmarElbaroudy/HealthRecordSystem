package service;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import persistence.MongoHandler;
import persistence.models.*;
import utility.Converter;
import utility.Encrypt;

import java.util.Scanner;

public class NodeServices {
    private NodeServices() {
    }

    public static int getNextCommand(Scanner sc) {
        System.out.println("\n\n\n");
        System.out.println("""
                Please Select A Command
                1 -> Generate public and private key pair
                2 -> Register your clinic with the certificate authority
                3 -> Create, sign and encrypt transaction
                4 -> Mine Block
                5 -> Trace transactions
                6 -> Trace and decrypt transactions
                7 -> Trace patient ID and decrypt transactions
                8 -> Exit""");

        return Integer.parseInt(sc.nextLine());
    }

    public static void generateAndPrintKeyPair() {
        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            System.out.println("EC-public key is :");
            System.out.println(Converter.getECKeyAsString(keyPair.getPublicKey()));
            System.out.println("EC-private key is :");
            System.out.println(Converter.getECKeyAsString(keyPair.getPrivateKey()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void registerEntityAtCA(Scanner sc, MongoHandler handler) {
        System.out.println("please enter your signing public key");
        String publicKey = sc.nextLine();

        System.out.println("your shared  AES key is :");
        String symmetricKey = Encrypt.createAESKey();
        System.out.println(symmetricKey);
        System.out.println("your shared initialization vector is :");
        String initVector = Encrypt.createInitializationVector();
        System.out.println(initVector);

        ClinicCredentials clinicCredentials = new ClinicCredentials(publicKey,
                initVector, symmetricKey);

        handler.saveClinic(clinicCredentials);
    }

    public static Transaction createTransaction(Scanner sc, MongoHandler handler) {
        System.out.println("""
                please enter the type of transaction you want to create
                1 -> Patient Info
                2 -> Visit Info""");

        int txType = Integer.parseInt(sc.nextLine());
        Info payLoad = txType == 1 ? getPatientInfoFromConsole(sc) : null;
        payLoad = txType == 2 ? getVisitInfoFromConsole(sc) : payLoad;

        System.out.println("please enter your signing private key");
        String signPrivKey = sc.nextLine();

        System.out.println("please enter your AES key");
        String symmetricKey = sc.nextLine();
        System.out.println("please enter your InitVector");
        String initVector = sc.nextLine();

        assert payLoad != null;

        return TransactionServices.createTransaction(
                payLoad, signPrivKey, symmetricKey, initVector, handler);
    }

    public static void trace(Scanner sc, MongoHandler handler) {
        System.out.println("please enter the signing public key to be traced");
        String signPubKey = sc.nextLine();
        BlockServices.trace(signPubKey, handler);
    }

    public static void traceAndDecrypt(Scanner sc, MongoHandler handler) {
        System.out.println("please enter the signing public key to be traced");
        String signPubKey = sc.nextLine();
        System.out.println("please enter the AES key");
        String symmetricKey = sc.nextLine();
        System.out.println("please enter the initial vector");
        String initVector = sc.nextLine();
        BlockServices.traceAndDecrypt(signPubKey, symmetricKey,
                initVector, handler, null);
    }

    public static void tracePatientAndDecrypt(Scanner sc, MongoHandler handler) {
        System.out.println("please enter the signing public key to be traced");
        String signPubKey = sc.nextLine();
        System.out.println("please enter the AES key");
        String symmetricKey = sc.nextLine();
        System.out.println("please enter the initial vector");
        String initVector = sc.nextLine();
        System.out.println("please enter the patient ID");
        String patientID = sc.nextLine();
        BlockServices.traceAndDecrypt(signPubKey, symmetricKey,
                initVector, handler, patientID);
    }

    public static boolean gracefullyShutDown(Scanner sc, MongoHandler handler) {
        sc.close();
        handler.closeConnection();
        return false;
    }

    private static VisitInfo getVisitInfoFromConsole(Scanner sc) {
        System.out.println("enter patient Id");
        String patientId = sc.nextLine();
        System.out.println("enter visit date");
        String visitDate = sc.nextLine();
        System.out.println("enter diagnosis");
        String diagnosis = sc.nextLine();
        System.out.println("enter prognosis");
        String prognosis = sc.nextLine();
        System.out.println("enter prescription");
        String prescription = sc.nextLine();
        System.out.println("enter assigned Dr. Name");
        String assignedDoctor = sc.nextLine();

        return new VisitInfo(patientId, visitDate, diagnosis,
                prognosis, prescription, assignedDoctor);
    }

    private static PatientInfo getPatientInfoFromConsole(Scanner sc) {
        System.out.println("enter patient Id");
        String patientId = sc.nextLine();
        System.out.println("enter name");
        String name = sc.nextLine();
        System.out.println("enter gender");
        String gender = sc.nextLine();
        System.out.println("enter phone number");
        String phone = sc.nextLine();
        System.out.println("enter weight");
        int weight = Integer.parseInt(sc.nextLine());
        System.out.println("enter yes if has medical insurance no otherwise");
        boolean hasMedicalInsurance = sc.nextLine().equalsIgnoreCase("yes");

        return new PatientInfo(patientId, name, phone,
                gender, weight, hasMedicalInsurance);
    }
}
