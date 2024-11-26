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
    // Get DAO instances through factory
    private final RegistrationDAO registrationDAO;
    private final StudentDAO studentDAO;
    private final ProgramDAO programDAO;

    public RegistrationBOImpl() {
        // Get DAO instances from factory
        this.registrationDAO = (RegistrationDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.REGISTRATION);
        this.studentDAO = (StudentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.STUDENT);
        this.programDAO = (ProgramDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.PROGRAM);
    }

    @Override
    public boolean saveRegistration(RegistrationDTO dto) throws Exception {
        try {
            // First check if student exists
            Student student = studentDAO.search(dto.getStudentId());
            if (student == null) {
                throw new Exception("Student not found with ID: " + dto.getStudentId());
            }

            // Then check if program exists
            Program program = programDAO.search(dto.getProgramId());
            if (program == null) {
                throw new Exception("Program not found with ID: " + dto.getProgramId());
            }

            // Check if student is already registered for this program
            if (registrationDAO.existsByStudentAndProgram(dto.getStudentId(), dto.getProgramId())) {
                throw new Exception("Student is already registered for this program");
            }

            // Create registration entity
            Registration registration = new Registration();
            registration.setStudent(student);
            registration.setProgram(program);
            registration.setRegistrationDate(dto.getRegistrationDate());
            registration.setPaymentStatus(dto.getPaymentStatus());

            return registrationDAO.save(registration);
        } catch (Exception e) {
            throw new Exception("Error saving registration: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateRegistration(RegistrationDTO dto) throws Exception {
        try {
            // Verify student exists
            Student student = studentDAO.search(dto.getStudentId());
            if (student == null) {
                throw new Exception("Student not found with ID: " + dto.getStudentId());
            }

            // Verify program exists
            Program program = programDAO.search(dto.getProgramId());
            if (program == null) {
                throw new Exception("Program not found with ID: " + dto.getProgramId());
            }

            // Create registration entity with updated information
            Registration registration = new Registration();
            registration.setRegistrationId(dto.getRegistrationId());
            registration.setStudent(student);
            registration.setProgram(program);
            registration.setRegistrationDate(dto.getRegistrationDate());
            registration.setPaymentStatus(dto.getPaymentStatus());

            return registrationDAO.update(registration);
        } catch (Exception e) {
            throw new Exception("Error updating registration: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteRegistration(String id) throws Exception {
        try {
            return registrationDAO.delete(id);
        } catch (Exception e) {
            throw new Exception("Error deleting registration: " + e.getMessage(), e);
        }
    }

    @Override
    public RegistrationDTO searchRegistration(String id) throws Exception {
        try {
            Registration registration = registrationDAO.search(id);
            return registration != null ? convertToDTO(registration) : null;
        } catch (Exception e) {
            throw new Exception("Error searching registration: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RegistrationDTO> getAllRegistrations() throws Exception {
        try {
            List<Registration> registrations = registrationDAO.getAll();
            return convertToDTOList(registrations);
        } catch (Exception e) {
            throw new Exception("Error getting all registrations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RegistrationDTO> getRegistrationsByStudent(String studentId) throws Exception {
        try {
            List<Registration> registrations = registrationDAO.findByStudentId(studentId);
            return convertToDTOList(registrations);
        } catch (Exception e) {
            throw new Exception("Error getting registrations by student: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RegistrationDTO> getRegistrationsByProgram(String programId) throws Exception {
        try {
            List<Registration> registrations = registrationDAO.findByProgramId(programId);
            return convertToDTOList(registrations);
        } catch (Exception e) {
            throw new Exception("Error getting registrations by program: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RegistrationDTO> getRegistrationsByPaymentStatus(Registration.PaymentStatus status) throws Exception {
        try {
            List<Registration> registrations = registrationDAO.findByPaymentStatus(status);
            return convertToDTOList(registrations);
        } catch (Exception e) {
            throw new Exception("Error getting registrations by status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isStudentRegisteredToProgram(String studentId, String programId) throws Exception {
        try {
            return registrationDAO.existsByStudentAndProgram(studentId, programId);
        } catch (Exception e) {
            throw new Exception("Error checking student registration: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RegistrationDTO> getRecentRegistrations() throws Exception {
        try {
            List<Registration> registrations = registrationDAO.findRecentRegistrations();
            return convertToDTOList(registrations);
        } catch (Exception e) {
            throw new Exception("Error getting recent registrations: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer getNextRegistrationId() {
        try {
            return registrationDAO.getNextId();
        } catch (Exception e) {
            e.printStackTrace();
            return 1; // Default to 1 if error occurs
        }
    }

    private RegistrationDTO convertToDTO(Registration registration) {
        if (registration == null) return null;

        RegistrationDTO dto = new RegistrationDTO();
        dto.setRegistrationId(registration.getRegistrationId());
        dto.setStudentId(registration.getStudent().getStudentId().toString());
        dto.setProgramId(registration.getProgram().getProgramId());
        dto.setRegistrationDate(registration.getRegistrationDate());
        dto.setPaymentStatus(registration.getPaymentStatus());

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

    private List<RegistrationDTO> convertToDTOList(List<Registration> registrations) {
        List<RegistrationDTO> dtoList = new ArrayList<>();
        for (Registration registration : registrations) {
            RegistrationDTO dto = convertToDTO(registration);
            if (dto != null) {
                dtoList.add(dto);
            }
        }
        return dtoList;
    }
}