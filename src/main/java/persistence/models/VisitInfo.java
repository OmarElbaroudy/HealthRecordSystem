package persistence.models;

import java.util.Objects;

public class VisitInfo extends Info{
    private final String patientId;
    private final String visitDate;
    private final String diagnosis;
    private final String prognosis;
    private final String prescription;
    private final String assignedDoctor;

    public VisitInfo(String patientId, String visitDate, String diagnosis,
                     String prognosis, String prescription, String assignedDoctor) {
        this.patientId = patientId;
        this.visitDate = visitDate;
        this.diagnosis = diagnosis;
        this.prognosis = prognosis;
        this.prescription = prescription;
        this.assignedDoctor = assignedDoctor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitInfo visitInfo = (VisitInfo) o;
        return Objects.equals(patientId, visitInfo.patientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, visitDate, diagnosis, prognosis, prescription, assignedDoctor);
    }

    @Override
    public String toString() {
        return "VisitInfo{" +
                "patientId='" + patientId + '\'' +
                ", visit date='" + visitDate + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", prognosis='" + prognosis + '\'' +
                ", prescription='" + prescription + '\'' +
                ", assignedDoctor='" + assignedDoctor + '\'' +
                '}';
    }
}
