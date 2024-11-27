package lk.ijse.dao.custom.impl;

import lk.ijse.config.FactoryConfiguration;
import lk.ijse.dao.custom.RegistrationDAO;
import lk.ijse.entity.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class RegistrationDAOImpl implements RegistrationDAO {

    @Override
    public boolean save(Registration entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            // 1. Verify and set the Student
            Student student = session.get(Student.class, entity.getStudent().getStudentId());
            if (student == null) {
                throw new RuntimeException("Student not found");
            }
            entity.setStudent(student);

            // 2. Verify and set Programs for each Registration Detail
            for (RegistrationDetails detail : entity.getRegistrationDetails()) {
                Program program = session.get(Program.class, detail.getProgram().getProgramId());
                if (program == null) {
                    throw new RuntimeException("Program not found: " + detail.getProgram().getProgramId());
                }
                detail.setProgram(program);
                detail.setRegistration(entity);  // Set bidirectional relationship
            }

            // 3. Set Registration reference for Payments
            for (Payment payment : entity.getPayments()) {
                payment.setRegistration(entity);  // Set bidirectional relationship
            }

            // 4. Save the Registration (cascade will handle details and payments)
            session.save(entity);

            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error saving registration: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(Registration entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Student student = session.get(Student.class, entity.getStudent().getStudentId());
            if (student == null) {
                throw new RuntimeException("Student not found");
            }
            entity.setStudent(student);
            session.update(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error updating registration: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Registration registration = session.get(Registration.class, Integer.parseInt(id));
            if (registration != null) {
                session.delete(registration);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error deleting registration: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public Registration search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(Registration.class, Integer.parseInt(id));
        } catch (Exception e) {
            throw new RuntimeException("Error searching registration: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    // RegistrationDAOImpl.java
    @Override
    public List<Registration> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            // Using JOIN FETCH to avoid the MultipleBagFetchException
            Query<Registration> query = session.createQuery(
                    "SELECT DISTINCT r FROM Registration r " +
                            "LEFT JOIN FETCH r.registrationDetails " +
                            "LEFT JOIN FETCH r.payments " +
                            "LEFT JOIN FETCH r.student " +
                            "ORDER BY r.id DESC",
                    Registration.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all registrations: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public String getLastId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Integer> query = session.createQuery(
                    "SELECT r.id FROM Registration r ORDER BY r.id DESC",
                    Integer.class
            );
            query.setMaxResults(1);
            Integer lastId = query.uniqueResult();
            return lastId != null ? String.format("%03d", lastId) : "000";
        } catch (Exception e) {
            throw new RuntimeException("Error getting last registration id: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Registration> findByStudent(String studentId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Registration> query = session.createQuery(
                    "FROM Registration r WHERE r.student.studentId = :studentId",
                    Registration.class
            );
            query.setParameter("studentId", Integer.parseInt(studentId));
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding registrations by student: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
}