package lk.ijse.bo.custom.impl;

import lk.ijse.bo.custom.ProgramRegistrationBO;
import lk.ijse.dao.DAOFactory;
import lk.ijse.dao.custom.ProgramRegistrationDAO;
import lk.ijse.dao.custom.StudentDAO;
import lk.ijse.dao.custom.ProgramDAO;
import lk.ijse.dto.ProgramRegistrationDTO;
import lk.ijse.entity.ProgramRegistration;
import lk.ijse.entity.Student;
import lk.ijse.entity.Program;
import lk.ijse.entity.Registration.PaymentStatus;
import java.util.ArrayList;
import java.util.List;

public class ProgramRegistrationBOImpl implements ProgramRegistrationBO {
    private final ProgramRegistrationDAO programRegistrationDAO = (ProgramRegistrationDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.PROGRAM_REGISTRATION);
    private final StudentDAO studentDAO = (StudentDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.STUDENT);
    private final ProgramDAO programDAO = (ProgramDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.PROGRAM);

    @Override
    public boolean saveProgramRegistration(ProgramRegistrationDTO dto) throws Exception {
        Student student = studentDAO.search(dto.getStudentId().toString());
        if (student == null) {
            throw new Exception("Student not found!");
        }

        Program program = programDAO.search(dto.getProgramId());
        if (program == null) {
            throw new Exception("Program not found!");
        }

        ProgramRegistration registration = new ProgramRegistration();
        registration.setStudent(student);
        registration.setProgramId(dto.getProgramId());
        registration.setRegistrationDate(dto.getRegistrationDate());
        registration.setPaymentStatus(dto.getPaymentStatus());

        return programRegistrationDAO.save(registration);
    }

    @Override
    public boolean updateProgramRegistration(ProgramRegistrationDTO dto) throws Exception {
        Student student = studentDAO.search(dto.getStudentId().toString());
        if (student == null) {
            throw new Exception("Student not found!");
        }

        Program program = programDAO.search(dto.getProgramId());
        if (program == null) {
            throw new Exception("Program not found!");
        }

        ProgramRegistration registration = new ProgramRegistration();
        registration.setRegistrationId(dto.getRegistrationId());
        registration.setStudent(student);
        registration.setProgramId(dto.getProgramId());
        registration.setRegistrationDate(dto.getRegistrationDate());
        registration.setPaymentStatus(dto.getPaymentStatus());

        return programRegistrationDAO.update(registration);
    }

    @Override
    public boolean deleteProgramRegistration(String id) throws Exception {
        return programRegistrationDAO.delete(id);
    }

    @Override
    public ProgramRegistrationDTO searchProgramRegistration(String id) throws Exception {
        ProgramRegistration registration = programRegistrationDAO.search(id);
        return registration != null ? convertToDTO(registration) : null;
    }

    @Override
    public List<ProgramRegistrationDTO> getAllProgramRegistrations() throws Exception {
        return convertToDTOList(programRegistrationDAO.getAll());
    }

    @Override
    public List<ProgramRegistrationDTO> getProgramRegistrationsByStudent(Integer studentId) throws Exception {
        return convertToDTOList(programRegistrationDAO.findByStudentId(studentId));
    }

    @Override
    public List<ProgramRegistrationDTO> getProgramRegistrationsByProgram(String programId) throws Exception {
        return convertToDTOList(programRegistrationDAO.findByProgramId(programId));
    }

    @Override
    public List<ProgramRegistrationDTO> getProgramRegistrationsByPaymentStatus(PaymentStatus status) throws Exception {
        return convertToDTOList(programRegistrationDAO.findByPaymentStatus(status));
    }

    @Override
    public boolean isStudentRegisteredToProgram(Integer studentId, String programId) throws Exception {
        return programRegistrationDAO.existsByStudentAndProgram(studentId, programId);
    }

    @Override
    public List<ProgramRegistrationDTO> getRecentProgramRegistrations() throws Exception {
        return convertToDTOList(programRegistrationDAO.findRecentRegistrations());
    }

    private ProgramRegistrationDTO convertToDTO(ProgramRegistration registration) {
        ProgramRegistrationDTO dto = new ProgramRegistrationDTO();
        dto.setRegistrationId(registration.getRegistrationId());
        dto.setStudentId(registration.getStudentId());
        dto.setProgramId(registration.getProgramId());
        dto.setRegistrationDate(registration.getRegistrationDate());
        dto.setPaymentStatus(registration.getPaymentStatus());
        dto.setCreatedAt(registration.getCreatedAt());
        dto.setUpdatedAt(registration.getUpdatedAt());

        // Set additional display information
        Student student = registration.getStudent();
        Program program = registration.getProgram();

        if (student != null) {
            dto.setStudentName(student.getFirstName() + " " + student.getLastName());
        }

        if (program != null) {
            dto.setProgramName(program.getProgramName());
            dto.setProgramDuration(program.getDurationMonths());
            dto.setProgramFee(program.getFee());
        }

        return dto;
    }

    private List<ProgramRegistrationDTO> convertToDTOList(List<ProgramRegistration> registrations) {
        List<ProgramRegistrationDTO> dtoList = new ArrayList<>();
        for (ProgramRegistration registration : registrations) {
            dtoList.add(convertToDTO(registration));
        }
        return dtoList;
    }
}