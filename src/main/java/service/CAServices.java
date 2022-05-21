package service;

import persistence.MongoHandler;
import persistence.models.Block;
import persistence.models.ClinicCredentials;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static service.BlockServices.getLastBlock;
import static service.BlockServices.hash;

public class CAServices {
    private CAServices() {
    }

    public static boolean notAssignedInitVector(String initVector, ClinicCredentials clinicCredentials) {
        if(!clinicCredentials.getInitVector().equals(initVector)){
            System.out.println("the initial Vector you have entered is not the one" +
                    " that was generated for you. Please use the designated init vector");
            return true;
        }
        return false;
    }

    public static boolean notAssignedAESKey(String symmetricKey, ClinicCredentials clinicCredentials) {
        if(!clinicCredentials.getSymmetricKey().equals(symmetricKey)){
            System.out.println("the AES key you have entered is not the one" +
                    " that was generated for you. Please use the designated AES key");
            return true;
        }
        return false;
    }

    public static boolean notRegistered(ClinicCredentials clinicCredentials) {
        if(clinicCredentials == null){
            System.out.println("you haven't registered your public key with" +
                    " the certificate authority please register and retry");
            return true;
        }
        return false;
    }

    public static boolean isPatientInfo(String payLoad){
        return payLoad.contains("PatientInfo");
    }

    public static boolean isVisitInfo(String payLoad){
        return payLoad.contains("VisitInfo");
    }

    public static boolean isSamePatientId(String fstPayLoad, String sndPayLoad){
        return Objects.equals(extractPatientId(fstPayLoad), extractPatientId(sndPayLoad));
    }

    public static String extractPatientId(String payLoad){
        String[] splittedPayLoad = payLoad.split(",");
        Optional<String> patientId = Arrays.stream(splittedPayLoad).
                filter(str -> str.contains("patientId")).findAny();

        if(patientId.isEmpty()) return null;
        int idx = patientId.get().lastIndexOf("=");
        return patientId.get().substring(idx + 1);
    }

    public static boolean validateMinedBlock(Block block, MongoHandler handler){
        Block lst = getLastBlock(handler);

        int difficulty = Integer.parseInt(
                Objects.requireNonNull(System.getenv("DIFFICULTY")));

        boolean flag = lst.getIdx() == block.getIdx() - 1;
        String hashLast = hash(lst.toString());
        flag &= hashLast.equals(block.getMetaData().getPreviousBlockHash());
        flag &= difficulty == block.getMetaData().getDifficulty();

        String hashVal = hash(block.toString());
        for (int i = 0; i < difficulty; i++) {
            flag &= hashVal.charAt(i) == '0';
        }

        return flag;
    }
}
