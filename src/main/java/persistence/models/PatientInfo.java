package persistence.models;

import java.util.Objects;

public class PatientInfo extends Info{
    private final String patientId;
    private final String name;
    private final String gender;
    private final String phone;
    private final int weight;
    private final boolean hasMedicalInsurance;

    public PatientInfo(String patientId, String name, String phone,
                       String gender, int weight, boolean hasMedicalInsurance){

        this.patientId = patientId;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.weight = weight;
        this.hasMedicalInsurance = hasMedicalInsurance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientInfo that = (PatientInfo) o;
        return weight == that.weight &&
                hasMedicalInsurance == that.hasMedicalInsurance &&
                Objects.equals(patientId, that.patientId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, name, gender, phone, weight, hasMedicalInsurance);
    }

    @Override
    public String toString() {
        return "PatientInfo{" +
                "patientId='" + patientId + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                ", weight=" + weight +
                ", hasMedicalInsurance=" + hasMedicalInsurance +
                '}';
    }
}
