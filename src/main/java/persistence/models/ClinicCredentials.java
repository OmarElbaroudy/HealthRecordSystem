package persistence.models;

import java.util.Objects;

public class ClinicCredentials {
    private String identifierPK; //HEX
    private String initVector; //64-encoded
    private String symmetricKey; //64-encoded

    public ClinicCredentials() {
    }

    public ClinicCredentials(String identifierPK, String initVector, String symmetricKey) {
        this.identifierPK = identifierPK;
        this.initVector = initVector;
        this.symmetricKey = symmetricKey;
    }

    public String getIdentifierPK() {
        return identifierPK;
    }

    public void setIdentifierPK(String identifierPK) {
        this.identifierPK = identifierPK;
    }

    public String getInitVector() {
        return initVector;
    }

    public void setInitVector(String initVector) {
        this.initVector = initVector;
    }

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(String symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClinicCredentials that = (ClinicCredentials) o;
        return Objects.equals(identifierPK, that.identifierPK);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierPK, initVector, symmetricKey);
    }
}
