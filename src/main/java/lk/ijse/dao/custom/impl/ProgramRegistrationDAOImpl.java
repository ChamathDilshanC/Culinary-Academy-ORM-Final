package lk.ijse.dao.custom.impl;

import lk.ijse.config.FactoryConfiguration;
import lk.ijse.dao.custom.ProgramRegistrationDAO;
import lk.ijse.entity.ProgramRegistration;
import lk.ijse.entity.Registration.PaymentStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class ProgramRegistrationDAOImpl implements ProgramRegistrationDAO {

    @Override
    public boolean save(ProgramRegistration programRegistration) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(programRegistration);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new Exception("Failed to save program registration: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(ProgramRegistration programRegistration) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(programRegistration);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new Exception("Failed to update program registration: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            ProgramRegistration registration = session.get(ProgramRegistration.class, Integer.valueOf(id));
            if (registration != null) {
                session.remove(registration);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            transaction.rollback();
            throw new Exception("Failed to delete program registration: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public ProgramRegistration search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(ProgramRegistration.class, Integer.valueOf(id));
        } catch (Exception e) {
            throw new Exception("Failed to search program registration: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<ProgramRegistration> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "FROM ProgramRegistration pr ORDER BY pr.registrationDate DESC";
            Query<ProgramRegistration> query = session.createQuery(hql, ProgramRegistration.class);
            return query.list();
        } catch (Exception e) {
            throw new Exception("Failed to get all program registrations: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<ProgramRegistration> findByStudentId(Integer studentId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "FROM ProgramRegistration pr WHERE pr.studentId = :studentId " +
                    "ORDER BY pr.registrationDate DESC";
            Query<ProgramRegistration> query = session.createQuery(hql, ProgramRegistration.class);
            query.setParameter("studentId", studentId);
            return query.list();
        } catch (Exception e) {
            throw new Exception("Failed to find program registrations by student: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<ProgramRegistration> findByProgramId(String programId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "FROM ProgramRegistration pr WHERE pr.programId = :programId " +
                    "ORDER BY pr.registrationDate DESC";
            Query<ProgramRegistration> query = session.createQuery(hql, ProgramRegistration.class);
            query.setParameter("programId", programId);
            return query.list();
        } catch (Exception e) {
            throw new Exception("Failed to find program registrations by program: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<ProgramRegistration> findByPaymentStatus(PaymentStatus status) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "FROM ProgramRegistration pr WHERE pr.paymentStatus = :status " +
                    "ORDER BY pr.registrationDate DESC";
            Query<ProgramRegistration> query = session.createQuery(hql, ProgramRegistration.class);
            query.setParameter("status", status);
            return query.list();
        } catch (Exception e) {
            throw new Exception("Failed to find program registrations by status: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public boolean existsByStudentAndProgram(Integer studentId, String programId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "SELECT COUNT(pr) FROM ProgramRegistration pr " +
                    "WHERE pr.studentId = :studentId AND pr.programId = :programId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("studentId", studentId);
            query.setParameter("programId", programId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            throw new Exception("Failed to check existing program registration: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<ProgramRegistration> findRecentRegistrations() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "FROM ProgramRegistration pr ORDER BY pr.registrationDate DESC";
            Query<ProgramRegistration> query = session.createQuery(hql, ProgramRegistration.class);
            query.setMaxResults(10);
            return query.list();
        } catch (Exception e) {
            throw new Exception("Failed to get recent program registrations: " + e.getMessage());
        } finally {
            session.close();
        }
    }
}