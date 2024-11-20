package lk.ijse.dao.custom.impl;

import lk.ijse.config.FactoryConfiguration;
import lk.ijse.dao.custom.UserDAO;
import lk.ijse.entity.User;
import lk.ijse.entity.User.Role;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean save(User entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(User entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            User user = session.get(User.class, Integer.parseInt(id));
            if (user != null) {
                session.delete(user);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public User search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(User.class, Integer.parseInt(id));
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<User> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public User findByEmail(String email) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<User> query = session.createQuery(
                    "FROM User WHERE email = :email",
                    User.class
            );
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public User findByUsername(String username) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<User> query = session.createQuery(
                    "FROM User WHERE username = :username",
                    User.class
            );
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean existsByEmail(String email) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM User WHERE email = :email",
                    Long.class
            );
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean existsByUsername(String username) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM User WHERE username = :username",
                    Long.class
            );
            query.setParameter("username", username);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateLastLogin(Integer userId, LocalDateTime lastLogin) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Query query = session.createQuery(
                    "UPDATE User SET lastLogin = :lastLogin WHERE userId = :userId"
            );
            query.setParameter("lastLogin", lastLogin);
            query.setParameter("userId", userId);
            int result = query.executeUpdate();
            transaction.commit();
            return result > 0;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updatePassword(Integer userId, String newPassword) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Query query = session.createQuery(
                    "UPDATE User SET password = :password, updatedAt = :updatedAt WHERE userId = :userId"
            );
            query.setParameter("password", newPassword);
            query.setParameter("updatedAt", LocalDateTime.now());
            query.setParameter("userId", userId);
            int result = query.executeUpdate();
            transaction.commit();
            return result > 0;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<User> findByRole(Role role) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<User> query = session.createQuery(
                    "FROM User WHERE role = :role",
                    User.class
            );
            query.setParameter("role", role);
            return query.list();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateUserRole(Integer userId, Role newRole) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Query query = session.createQuery(
                    "UPDATE User SET role = :role, updatedAt = :updatedAt WHERE userId = :userId"
            );
            query.setParameter("role", newRole);
            query.setParameter("updatedAt", LocalDateTime.now());
            query.setParameter("userId", userId);
            int result = query.executeUpdate();
            transaction.commit();
            return result > 0;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Integer getCurrentId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Integer> query = session.createQuery(
                    "SELECT MAX(u.userId) FROM User u",
                    Integer.class
            );
            Integer currentId = query.uniqueResult();
            return (currentId == null) ? 0 : currentId;
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Integer getUserIdByUsername(String username) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Integer> query = session.createQuery(
                    "SELECT u.userId FROM User u WHERE u.username = :username",
                    Integer.class
            );
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

}