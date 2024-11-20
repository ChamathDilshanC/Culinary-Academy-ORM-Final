package lk.ijse.dao.custom;

import lk.ijse.dao.CrudDAO;
import lk.ijse.entity.User;
import lk.ijse.entity.User.Role;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDAO extends CrudDAO<User> {
    User findByEmail(String email) throws Exception;
    User findByUsername(String username) throws Exception;
    boolean existsByEmail(String email) throws Exception;
    boolean existsByUsername(String username) throws Exception;
    boolean updateLastLogin(Integer userId, LocalDateTime lastLogin) throws Exception;
    boolean updatePassword(Integer userId, String newPassword) throws Exception;
    List<User> findByRole(Role role) throws Exception;
    boolean updateUserRole(Integer userId, Role newRole) throws Exception;
    Integer getCurrentId() throws Exception;
    Integer getUserIdByUsername(String username) throws Exception;
}