package persistence.models;


import utility.MerkelTree;

import java.util.Objects;

public class Block {
    private MerkelTree transactions;
    private MetaData metaData;

    public Block(MetaData metaData, MerkelTree transactions) {
        this.metaData = metaData;
        this.transactions = transactions;
        metaData.setMerkleRootHash(transactions.getRoot());
    }

    public Block() {
    }

    public int getIdx() {
        return this.metaData.getBlockIndex();
    }

    public MerkelTree getTransactions() {
        return transactions;
    }

    public void setTransactions(MerkelTree transactions) {
        this.transactions = transactions;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public String toDecryptedString(String symmetricKey, String initVector) {
        return "Block{" +
                "transactions=" +
                transactions.getFirstTransaction().
                        toDecryptedString(symmetricKey, initVector) +
                ", metaData=" + metaData.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return Objects.equals(metaData, block.metaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactions, metaData);
    }

    @Override
    public String toString() {
        return "Block{" +
                "transactions=" + transactions.toString() +
                ", metaData=" + metaData.toString() +
                '}';
    }
}
