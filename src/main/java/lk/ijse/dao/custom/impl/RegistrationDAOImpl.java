package lk.ijse.dao.custom.impl;

import lk.ijse.dao.custom.RegistrationDAO;
import lk.ijse.entity.Registration;
import lk.ijse.entity.Registration.PaymentStatus;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class RegistrationDAOImpl implements RegistrationDAO {
    private Session session;

    @Override
    public boolean save(Registration registration) throws Exception {
        session.persist(registration);
        return true;
    }

    @Override
    public boolean update(Registration registration) throws Exception {
        session.merge(registration);
        return true;
    }

    @Override
    public boolean delete(String id) throws Exception {
        Registration registration = session.get(Registration.class, Integer.valueOf(id));
        if (registration != null) {
            session.remove(registration);
            return true;
        }
        return false;
    }

    @Override
    public Registration search(String id) throws Exception {
        return session.get(Registration.class, Integer.valueOf(id));
    }

    @Override
    public List<Registration> getAll() throws Exception {
        String hql = "FROM Registration r ORDER BY r.registrationDate DESC";
        Query<Registration> query = session.createQuery(hql, Registration.class);
        return query.list();
    }

    @Override
    public List<Registration> findByStudentId(String studentId) throws Exception {
        String hql = "FROM Registration r WHERE r.student.id = :studentId " +
                "ORDER BY r.registrationDate DESC";
        Query<Registration> query = session.createQuery(hql, Registration.class);
        query.setParameter("studentId", Integer.valueOf(studentId));
        return query.list();
    }

    @Override
    public List<Registration> findByProgramId(String programId) throws Exception {
        String hql = "FROM Registration r WHERE r.program.id = :programId " +
                "ORDER BY r.registrationDate DESC";
        Query<Registration> query = session.createQuery(hql, Registration.class);
        query.setParameter("programId", Integer.valueOf(programId));
        return query.list();
    }

    @Override
    public List<Registration> findByPaymentStatus(PaymentStatus status) throws Exception {
        String hql = "FROM Registration r WHERE r.paymentStatus = :status " +
                "ORDER BY r.registrationDate DESC";
        Query<Registration> query = session.createQuery(hql, Registration.class);
        query.setParameter("status", status);
        return query.list();
    }

    @Override
    public boolean existsByStudentAndProgram(String studentId, String programId) throws Exception {
        String hql = "SELECT COUNT(r) FROM Registration r " +
                "WHERE r.student.id = :studentId AND r.program.id = :programId";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("studentId", Integer.valueOf(studentId));
        query.setParameter("programId", Integer.valueOf(programId));
        return query.uniqueResult() > 0;
    }

    @Override
    public List<Registration> findRecentRegistrations() throws Exception {
        String hql = "FROM Registration r ORDER BY r.registrationDate DESC";
        Query<Registration> query = session.createQuery(hql, Registration.class);
        query.setMaxResults(10); // Get last 10 registrations
        return query.list();
    }
}