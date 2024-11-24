package lk.ijse.bo.custom.impl;

import lk.ijse.bo.custom.RegistrationBO;
import lk.ijse.dao.DAOFactory;
import lk.ijse.dao.custom.RegistrationDAO;
import lk.ijse.dao.custom.StudentDAO;
import lk.ijse.dao.custom.ProgramDAO;
import lk.ijse.dto.RegistrationDTO;
import lk.ijse.entity.Registration;
import lk.ijse.entity.Student;
import lk.ijse.entity.Program;
import java.util.ArrayList;
import java.util.List;

public class RegistrationBOImpl implements RegistrationBO {
    private final RegistrationDAO registrationDAO = (RegistrationDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.REGISTRATION);
    private final StudentDAO studentDAO = (StudentDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.STUDENT);
    private final ProgramDAO programDAO = (ProgramDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.PROGRAM);

    @Override
    public boolean saveRegistration(RegistrationDTO dto) throws Exception {
        Student student = studentDAO.search(dto.getStudentId());
        if (student == null) {
            throw new Exception("Student not found!");
        }

        Program program = programDAO.search(dto.getProgramId());
        if (program == null) {
            throw new Exception("Program not found!");
        }

        Registration registration = new Registration();
        registration.setStudent(student);
        registration.setProgram(program);
        registration.setRegistrationDate(dto.getRegistrationDate());
        registration.setPaymentStatus(dto.getPaymentStatus());

        return registrationDAO.save(registration);
    }

    @Override
    public boolean updateRegistration(RegistrationDTO dto) throws Exception {
        Student student = studentDAO.search(dto.getStudentId());
        if (student == null) {
            throw new Exception("Student not found!");
        }

        Program program = programDAO.search(dto.getProgramId());
        if (program == null) {
            throw new Exception("Program not found!");
        }

        Registration registration = new Registration();
        registration.setRegistrationId(dto.getRegistrationId());
        registration.setStudent(student);
        registration.setProgram(program);
        registration.setRegistrationDate(dto.getRegistrationDate());
        registration.setPaymentStatus(dto.getPaymentStatus());

        return registrationDAO.update(registration);
    }

    @Override
    public boolean deleteRegistration(String id) throws Exception {
        return registrationDAO.delete(id);
    }

    @Override
    public RegistrationDTO searchRegistration(String id) throws Exception {
        Registration registration = registrationDAO.search(id);
        return registration != null ? convertToDTO(registration) : null;
    }

    @Override
    public List<RegistrationDTO> getAllRegistrations() throws Exception {
        return convertToDTOList(registrationDAO.getAll());
    }

    @Override
    public List<RegistrationDTO> getRegistrationsByStudent(String studentId) throws Exception {
        return convertToDTOList(registrationDAO.findByStudentId(studentId));
    }

    @Override
    public List<RegistrationDTO> getRegistrationsByProgram(String programId) throws Exception {
        return convertToDTOList(registrationDAO.findByProgramId(programId));
    }

    @Override
    public List<RegistrationDTO> getRegistrationsByPaymentStatus(Registration.PaymentStatus status) throws Exception {
        return convertToDTOList(registrationDAO.findByPaymentStatus(status));
    }

    @Override
    public boolean isStudentRegisteredToProgram(String studentId, String programId) throws Exception {
        return registrationDAO.existsByStudentAndProgram(studentId, programId);
    }

    @Override
    public List<RegistrationDTO> getRecentRegistrations() throws Exception {
        return convertToDTOList(registrationDAO.findRecentRegistrations());
    }

    private RegistrationDTO convertToDTO(Registration registration) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setRegistrationId(registration.getRegistrationId());
        dto.setStudentId(String.valueOf(registration.getStudent().getStudentId()));
        dto.setProgramId(registration.getProgram().getProgramId());
        dto.setRegistrationDate(registration.getRegistrationDate());
        dto.setPaymentStatus(registration.getPaymentStatus());
        dto.setCreatedAt(registration.getCreatedAt());
        dto.setUpdatedAt(registration.getUpdatedAt());

        // Set additional display information
        Student student = registration.getStudent();
        Program program = registration.getProgram();

        dto.setStudentName(student.getFirstName() + " " + student.getLastName());
        dto.setProgramName(program.getProgramName());
        dto.setProgramDuration(program.getDurationMonths());
        dto.setProgramFee(program.getFee());

        return dto;
    }

    private List<RegistrationDTO> convertToDTOList(List<Registration> registrations) {
        List<RegistrationDTO> dtoList = new ArrayList<>();
        for (Registration registration : registrations) {
            dtoList.add(convertToDTO(registration));
        }
        return dtoList;
    }
}