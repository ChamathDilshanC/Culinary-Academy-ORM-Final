package lk.ijse.bo.custom.impl;

import lk.ijse.bo.custom.UserBO;
import lk.ijse.dao.DAOFactory;
import lk.ijse.dao.custom.UserDAO;
import lk.ijse.dto.UserDTO;
import lk.ijse.entity.User;
import lk.ijse.entity.User.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserBOImpl implements UserBO {
    private final UserDAO userDAO = (UserDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.USER);

    @Override
    public boolean saveUser(UserDTO dto) throws Exception {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setRole(Role.valueOf(dto.getRole().name()));
        user.setLastLogin(dto.getLastLogin());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userDAO.save(user);
    }

    @Override
    public boolean updateUser(UserDTO dto) throws Exception {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setRole(Role.valueOf(dto.getRole().name()));
        user.setLastLogin(dto.getLastLogin());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(LocalDateTime.now());

        return userDAO.update(user);
    }

    @Override
    public boolean deleteUser(String id) throws Exception {
        return userDAO.delete(id);
    }

    @Override
    public UserDTO searchUser(String id) throws Exception {
        User user = userDAO.search(id);
        if (user != null) {
            return new UserDTO(
                    user.getUserId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getRole(),
                    user.getLastLogin(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }
        return null;
    }

    @Override
    public List<UserDTO> getAllUsers() throws Exception {
        List<User> users = userDAO.getAll();
        ArrayList<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            userDTOs.add(new UserDTO(
                    user.getUserId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getRole(),
                    user.getLastLogin(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            ));
        }
        return userDTOs;
    }

    @Override
    public UserDTO findByEmail(String email) throws Exception {
        User user = userDAO.findByEmail(email);
        if (user != null) {
            return new UserDTO(
                    user.getUserId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getRole(),
                    user.getLastLogin(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }
        return null;
    }

    @Override
    public UserDTO findByUsername(String username) throws Exception {
        User user = userDAO.findByUsername(username);
        if (user != null) {
            return new UserDTO(
                    user.getUserId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getRole(),
                    user.getLastLogin(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }
        return null;
    }

    @Override
    public boolean existsByEmail(String email) throws Exception {
        return userDAO.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) throws Exception {
        return userDAO.existsByUsername(username);
    }
}