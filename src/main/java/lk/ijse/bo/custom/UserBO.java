package lk.ijse.bo.custom;

import lk.ijse.bo.SuperBO;
import lk.ijse.dto.UserDTO;
import java.util.List;

public interface UserBO extends SuperBO {
    boolean saveUser(UserDTO dto) throws Exception;
    boolean updateUser(UserDTO dto) throws Exception;
    boolean deleteUser(String id) throws Exception;
    UserDTO searchUser(String id) throws Exception;
    List<UserDTO> getAllUsers() throws Exception;
    UserDTO findByEmail(String email) throws Exception;
    UserDTO findByUsername(String username) throws Exception;
    boolean existsByEmail(String email) throws Exception;
    boolean existsByUsername(String username) throws Exception;
}