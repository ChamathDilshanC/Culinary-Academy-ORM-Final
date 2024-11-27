package lk.ijse.bo.custom;

import lk.ijse.bo.SuperBO;
import lk.ijse.dto.StudentDTO;
import java.util.List;

public interface StudentBO extends SuperBO {
    Integer getNextStudentId() throws Exception;
    boolean saveStudent(StudentDTO dto) throws Exception;
    boolean updateStudent(StudentDTO dto) throws Exception;
    boolean deleteStudent(String id) throws Exception;
    StudentDTO searchStudent(String id) throws Exception;
    List<StudentDTO> getAllStudents() throws Exception;
    boolean existsByEmail(String email) throws Exception;
    boolean existsByPhone(String phone) throws Exception;
    List<StudentDTO> searchStudentsByPhone(String searchText);
}