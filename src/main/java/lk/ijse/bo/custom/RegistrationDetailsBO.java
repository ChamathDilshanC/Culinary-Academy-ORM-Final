package lk.ijse.bo.custom;

import lk.ijse.bo.SuperBO;
import lk.ijse.dto.RegistrationDetailsDTO;
import java.util.List;

public interface RegistrationDetailsBO extends SuperBO {
    List<RegistrationDetailsDTO> getDetailsByRegistration(String registrationId) throws Exception;
    List<RegistrationDetailsDTO> getDetailsByProgram(String programId) throws Exception;
    List<RegistrationDetailsDTO> getAllRegistrationDetails() throws Exception;
}