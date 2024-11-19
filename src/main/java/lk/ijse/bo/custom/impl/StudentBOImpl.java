package lk.ijse.bo.custom.impl;

import lk.ijse.dao.DAOFactory;
import lk.ijse.dao.custom.StudentDAO;
import lk.ijse.dao.custom.UserDAO;
import lk.ijse.bo.custom.StudentBO;
import lk.ijse.dto.StudentDTO;
import lk.ijse.entity.Student;
import lk.ijse.entity.User;

import java.util.ArrayList;
import java.util.List;

public class StudentBOImpl implements StudentBO {
    private final StudentDAO studentDAO = (StudentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.STUDENT);
    private final UserDAO userDAO = (UserDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.USER);

    @Override
    public Integer getNextStudentId() throws Exception {
        return studentDAO.getCurrentId() + 1;
    }

    @Override
    public boolean saveStudent(StudentDTO dto) throws Exception {
        // Get the user entity
        User user = userDAO.search(dto.getUserId().toString());
        if (user == null) {
            throw new Exception("User not found");
        }

        // Convert DTO to Entity
        Student student = new Student();
        student.setUser(user);
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setAddress(dto.getAddress());
        student.setRegistrationDate(dto.getRegistrationDate());

        return studentDAO.save(student);
    }

    @Override
    public boolean updateStudent(StudentDTO dto) throws Exception {
        // Get the user entity
        User user = userDAO.search(dto.getUserId().toString());
        if (user == null) {
            throw new Exception("User not found");
        }

        Student student = new Student();
        student.setStudentId(dto.getStudentId());
        student.setUser(user);
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setAddress(dto.getAddress());
        student.setRegistrationDate(dto.getRegistrationDate());

        return studentDAO.update(student);
    }

    @Override
    public boolean deleteStudent(String id) throws Exception {
        return studentDAO.delete(id);
    }

    @Override
    public StudentDTO searchStudent(String id) throws Exception {
        Student student = studentDAO.search(id);
        if (student != null) {
            return new StudentDTO(
                    student.getStudentId(),
                    student.getUser().getUserId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getPhoneNumber(),
                    student.getAddress(),
                    student.getRegistrationDate()
            );
        }
        return null;
    }

    @Override
    public List<StudentDTO> getAllStudents() throws Exception {
        List<Student> students = studentDAO.getAll();
        ArrayList<StudentDTO> studentDTOs = new ArrayList<>();

        for (Student student : students) {
            studentDTOs.add(new StudentDTO(
                    student.getStudentId(),
                    student.getUser().getUserId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getPhoneNumber(),
                    student.getAddress(),
                    student.getRegistrationDate()
            ));
        }
        return studentDTOs;
    }


    @Override
    public boolean existsByEmail(String email) throws Exception {
        return studentDAO.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) throws Exception {
        return studentDAO.existsByPhone(phone);
    }

}
