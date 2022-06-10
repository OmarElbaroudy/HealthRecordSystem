package service;

import org.apache.commons.codec.digest.DigestUtils;
import org.web3j.crypto.ECKeyPair;
import persistence.MongoHandler;
import persistence.models.*;
import utility.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockServices {
    private BlockServices() {
    }

    public static void generateGenesisBlockIfDoesNotExist(MongoHandler handler) {
        if (handler.getBlock(0) != null) return;

        PatientInfo patientInfo = new PatientInfo(
                "-1", "-1", "-1",
                "-1", -1, false);

        String privKey = System.getenv("GENESIS_PRIVATE_KEY");
        String symmetricKey = System.getenv("GENESIS_AES_KEY");
        String initVector = System.getenv("GENESIS_IV");

        ECKeyPair keyPair = ECKeyPair.create(Converter.getECKeyFromString(privKey));

        String payLoad = Encrypt.doAESEncryption(
                patientInfo.toString(), symmetricKey, initVector);

        assert payLoad != null;
        Sign.SignatureData signatureData = Sign.signMessage(payLoad.getBytes(), keyPair);

        Transaction transaction = new Transaction(payLoad,
                Converter.getECKeyAsString(keyPair.getPublicKey()), signatureData);

        MetaData metaData = getMetaData();
        Block genesis = new Block(metaData,
                new MerkelTree(new ArrayList<>(List.of(transaction))));

        handler.saveBlock(genesis);
        System.out.println("Genesis Block : ");
        System.out.println(genesis);
    }

    private static MetaData getMetaData() {
        MetaData metaData = new MetaData();

        metaData.setBlockIndex(0);
        metaData.setTimestamp(Long.valueOf(System.getenv("GENESIS_TIMESTAMP")));
        metaData.setDifficulty(Integer.parseInt(System.getenv("DIFFICULTY")));
        metaData.setNonce(Integer.parseInt(System.getenv("GENESIS_NONCE")));
        metaData.setPreviousBlockHash(System.getenv("GENESIS_PREVIOUS_HASH"));

        return metaData;
    }

    public static Transaction mineBlock(Transaction t, MongoHandler handler) {
        if (t == null) {
            System.out.println("was not able to mine please create a transaction " +
                    "first to be mined!");
            return null;
        }

        if (!CAServices.validateTransactionSignature(t)) {
            System.out.println("invalid signature! the scriptPubKey doesn't" +
                    "correspond to the script signature");
        }

        String pubKey = t.getScriptPublicKey();
        ClinicCredentials clinicCredentials = handler.getClinic(pubKey);

        String decryptedPayLoad = t.getDecryptedPayLoad(
                clinicCredentials.getSymmetricKey(), clinicCredentials.getInitVector());

        if (CAServices.isPatientInfo(decryptedPayLoad)) {

            if (patientIdExists(handler, pubKey, clinicCredentials, decryptedPayLoad)) {
                System.out.println("this patient is already registered!" +
                        "you can only add visits for this patient");
                return null;
            }

        } else if (CAServices.isVisitInfo(decryptedPayLoad)) {

            if (!patientIdExists(handler, pubKey, clinicCredentials, decryptedPayLoad)) {
                System.out.println("this patient Id is not registered!" +
                        "please add his patient info before creating a visit");
                return null;
            }

        } else {
            System.out.println("this is a gibberish transaction please create a" +
                    "meaningful one");
            return null;
        }

        System.out.println("CA verifications successful!");
        Block minedBlock = getMinedBlock(t, handler);

        if (CAServices.validateMinedBlock(minedBlock, handler)) {
            System.out.println("CA validated mined block successfully");
            handler.saveBlock(minedBlock);
            System.out.println(minedBlock);
        } else {
            System.out.println("CA detected fraud during mined block validation");
            System.out.println("Mined block is rejected!");
        }

        return null;
    }

    public static void trace(String signPubKey, MongoHandler handler) {
        int idx = 0;
        Block block = handler.getBlock(idx);

        while (block != null) {
            Transaction t = block.getTransactions().getFirstTransaction();
            if (t.getScriptPublicKey().equals(signPubKey)) {
                System.out.println(block);
            }

            block = handler.getBlock(++idx);
        }
    }

    public static void traceAndDecrypt(String signPubKey, String symmetricKey,
                                       String initVector, MongoHandler handler,
                                       String patientID) {

        ClinicCredentials clinicCredentials = handler.getClinic(signPubKey);
        if (CAServices.notRegistered(clinicCredentials) ||
                CAServices.notAssignedAESKey(symmetricKey, clinicCredentials) ||
                CAServices.notAssignedInitVector(initVector, clinicCredentials)) return;

        int idx = 1;
        Block block = handler.getBlock(1);

        while (block != null) {
            Transaction t = block.getTransactions().getFirstTransaction();

            if(patientID != null &&
                    !Objects.equals(CAServices.extractPatientId(
                            t.getDecryptedPayLoad(symmetricKey, initVector))
                            , patientID)){
                continue;
            }

            if (t.getScriptPublicKey().equals(signPubKey)) {
                System.out.println(block.toDecryptedString(symmetricKey, initVector));
            }
            block = handler.getBlock(++idx);
        }
    }

    private static Block getMinedBlock(Transaction t, MongoHandler handler) {
        Block lst = getLastBlock(handler);
        String prevHash = hash(Objects.requireNonNull(lst).toString());

        int nonce = 0;
        int idx = lst.getIdx() + 1;
        int difficulty = Integer.parseInt(
                Objects.requireNonNull(System.getenv("DIFFICULTY")));

        for (boolean flag; true; nonce++) {
            MetaData data = new MetaData(idx, prevHash, nonce, difficulty);
            MerkelTree tree = new MerkelTree(new ArrayList<>(List.of(t)));
            Block b = new Block(data, tree);
            String hashedValue = hash(b.toString());
            flag = check(hashedValue, difficulty);
            if (flag) return b;
        }
    }

    private static boolean check(String s, int diff) {
        for (int i = 0; i < diff; i++) {
            if (s.charAt(i) != '0') return false;
        }
        return true;
    }

    public static String hash(String block) {
        return new DigestUtils("SHA3-256").digestAsHex(block);
    }

    public static Block getLastBlock(MongoHandler handler) {
        int idx = 0;
        Block lst = handler.getBlock(idx), b = lst;

        while (b != null) {
            lst = b;
            b = handler.getBlock(++idx);
        }

        return lst;
    }

    private static boolean patientIdExists(
            MongoHandler handler, String pubKey,
            ClinicCredentials clinicCredentials, String decryptedPayLoad) {

        int idx = 0;
        boolean exists = false;
        Block block = handler.getBlock(idx);

        while (block != null) {
            Transaction t2 = block.getTransactions().getFirstTransaction();

            if (t2.getScriptPublicKey().equals(pubKey)) {

                String decryptedPayLoad2 = t2.getDecryptedPayLoad(
                        clinicCredentials.getSymmetricKey(),
                        clinicCredentials.getInitVector());

                exists |= CAServices.isSamePatientId(
                        decryptedPayLoad, decryptedPayLoad2);
            }

            block = handler.getBlock(++idx);
        }

        return exists;
    }

}
