package service;

import org.web3j.crypto.ECKeyPair;
import persistence.MongoHandler;
import persistence.models.ClinicCredentials;
import persistence.models.Info;
import persistence.models.Transaction;
import utility.Converter;
import utility.Encrypt;
import utility.Sign;


public class TransactionServices {
    private TransactionServices() {}

    public static Transaction createTransaction(
            Info payLoad, String privKey, String symmetricKey,
            String initVector, MongoHandler handler) {

        ECKeyPair keyPair = ECKeyPair.create(Converter.getECKeyFromString(privKey));
        String pubKey = Converter.getECKeyAsString(keyPair.getPublicKey());

        ClinicCredentials clinicCredentials = handler.getClinic(pubKey);

        if (CAServices.notRegistered(clinicCredentials) ||
                CAServices.notAssignedAESKey(symmetricKey, clinicCredentials) ||
                CAServices.notAssignedInitVector(initVector, clinicCredentials)){
            return null;
        }

        String encryptedPayLoad =
                Encrypt.doAESEncryption(payLoad.toString(), symmetricKey, initVector);

        assert encryptedPayLoad != null;
        Sign.SignatureData signatureData =
                Sign.signMessage(encryptedPayLoad.getBytes(), keyPair);

        System.out.println("CA verifications successful!");
        return new Transaction(encryptedPayLoad, pubKey, signatureData);
    }

}
