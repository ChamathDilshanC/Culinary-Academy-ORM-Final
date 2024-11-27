package lk.ijse.bo.custom.impl;

import lk.ijse.bo.custom.RegistrationDetailsBO;
import lk.ijse.dao.DAOFactory;
import lk.ijse.dao.custom.RegistrationDetailsDAO;
import lk.ijse.dto.RegistrationDetailsDTO;
import lk.ijse.entity.RegistrationDetails;

import java.util.List;
import java.util.stream.Collectors;

public class RegistrationDetailsBOImpl implements RegistrationDetailsBO {
    private final RegistrationDetailsDAO registrationDetailsDAO = (RegistrationDetailsDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.REGISTRATION_DETAILS);

    @Override
    public List<RegistrationDetailsDTO> getDetailsByRegistration(String registrationId) throws Exception {
        return registrationDetailsDAO.findByRegistrationId(registrationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegistrationDetailsDTO> getDetailsByProgram(String programId) throws Exception {
        return registrationDetailsDAO.findByProgramId(programId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegistrationDetailsDTO> getAllRegistrationDetails() throws Exception {
        return registrationDetailsDAO.getAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RegistrationDetailsDTO convertToDTO(RegistrationDetails details) {
        return new RegistrationDetailsDTO(
                details.getId(),
                details.getRegistration().getId(),
                details.getProgram().getProgramId(),
                details.getProgram().getProgramName(),
                details.getProgram().getFee()
        );
    }
}