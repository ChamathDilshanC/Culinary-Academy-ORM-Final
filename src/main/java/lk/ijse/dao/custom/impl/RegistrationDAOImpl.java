package lk.ijse.dao.custom.impl;

import lk.ijse.config.FactoryConfiguration;
import lk.ijse.dao.custom.RegistrationDAO;
import lk.ijse.entity.Registration;
import lk.ijse.entity.Registration.PaymentStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RegistrationDAOImpl implements RegistrationDAO {
    @Override
    public boolean save(Registration registration) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(registration);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new Exception("Failed to save registration: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(Registration registration) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(registration);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new Exception("Failed to update registration: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Registration registration = session.get(Registration.class, Integer.valueOf(id));
            if (registration != null) {
                session.remove(registration);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            transaction.rollback();
            throw new Exception("Failed to delete registration: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public Registration search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(Registration.class, Integer.valueOf(id));
        } catch (Exception e) {
            throw new Exception("Failed to search registration: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<Registration> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "FROM Registration r ORDER BY r.registrationDate DESC";
            Query<Registration> query = session.createQuery(hql, Registration.class);
            return query.list();
        } catch (Exception e) {
            throw new Exception("Failed to get all registrations: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<Registration> findByStudentId(String studentId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "FROM Registration r WHERE r.student.id = :studentId " +
                    "ORDER BY r.registrationDate DESC";
            Query<Registration> query = session.createQuery(hql, Registration.class);
            query.setParameter("studentId", Integer.valueOf(studentId));
            return query.list();
        } catch (Exception e) {
            throw new Exception("Failed to find registrations by student: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<Registration> findByProgramId(String programId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "FROM Registration r WHERE r.program.id = :programId " +
                    "ORDER BY r.registrationDate DESC";
            Query<Registration> query = session.createQuery(hql, Registration.class);
            query.setParameter("programId", programId);
            return query.list();
        } catch (Exception e) {
            throw new Exception("Failed to find registrations by program: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<Registration> findByPaymentStatus(PaymentStatus status) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "FROM Registration r WHERE r.paymentStatus = :status " +
                    "ORDER BY r.registrationDate DESC";
            Query<Registration> query = session.createQuery(hql, Registration.class);
            query.setParameter("status", status);
            return query.list();
        } catch (Exception e) {
            throw new Exception("Failed to find registrations by status: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public boolean existsByStudentAndProgram(String studentId, String programId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "SELECT COUNT(r) FROM Registration r " +
                    "WHERE r.student.id = :studentId AND r.program.id = :programId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("studentId", Integer.valueOf(studentId));
            query.setParameter("programId", programId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            throw new Exception("Failed to check student registration: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<Registration> findRecentRegistrations() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "FROM Registration r ORDER BY r.registrationDate DESC";
            Query<Registration> query = session.createQuery(hql, Registration.class);
            query.setMaxResults(10);
            return query.list();
        } catch (Exception e) {
            throw new Exception("Failed to get recent registrations: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public Integer getNextId() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "SELECT COALESCE(MAX(r.registrationId), 0) FROM Registration r";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            return query.uniqueResult() + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            session.close();
        }
    }
}