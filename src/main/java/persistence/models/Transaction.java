package persistence.models;


import utility.Encrypt;
import utility.Sign;

import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private String txId;

    private String payLoad; //patientInfo or visitInfo
    private String scriptPublicKey; //HEX
    private Sign.SignatureData scriptSig;

    public Transaction() {
    }

    public Transaction(String payLoad,
                       String scriptPublicKey, Sign.SignatureData scriptSig) {

        this.txId = UUID.randomUUID().toString();
        this.payLoad = payLoad;
        this.scriptPublicKey = scriptPublicKey;
        this.scriptSig = scriptSig;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    public String getScriptPublicKey() {
        return scriptPublicKey;
    }

    public void setScriptPublicKey(String scriptPublicKey) {
        this.scriptPublicKey = scriptPublicKey;
    }

    public Sign.SignatureData getScriptSig() {
        return scriptSig;
    }

    public void setScriptSig(Sign.SignatureData scriptSig) {
        this.scriptSig = scriptSig;
    }

    public String toDecryptedString(String symmetricKey, String initVector){
        return "Transaction{" +
                "txId='" + txId + '\'' +
                ", payLoad='" + getDecryptedPayLoad(symmetricKey, initVector) + '\'' +
                ", scriptPublicKey='" + scriptPublicKey + '\'' +
                ", scriptSig=" + scriptSig +
                '}';
    }

    public String getDecryptedPayLoad(String symmetricKey, String initVector){
        return Encrypt.doAESDecryption(payLoad, symmetricKey, initVector);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(txId, that.txId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(txId, payLoad, scriptPublicKey, scriptSig);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "txId='" + txId + '\'' +
                ", payLoad='" + payLoad + '\'' +
                ", scriptPublicKey='" + scriptPublicKey + '\'' +
                ", scriptSig=" + scriptSig +
                '}';
    }
}
