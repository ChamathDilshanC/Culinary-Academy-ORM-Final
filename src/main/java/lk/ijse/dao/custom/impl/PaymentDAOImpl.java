package lk.ijse.dao.custom.impl;

import lk.ijse.dao.custom.PaymentDAO;
import lk.ijse.entity.Payment;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {
    private Session session;

    @Override
    public boolean save(Payment payment) throws Exception {
        session.persist(payment);
        return true;
    }

    @Override
    public boolean update(Payment payment) throws Exception {
        session.merge(payment);
        return true;
    }

    @Override
    public boolean delete(String id) throws Exception {
        Payment payment = session.get(Payment.class, Integer.valueOf(id));
        if (payment != null) {
            session.remove(payment);
            return true;
        }
        return false;
    }

    @Override
    public Payment search(String id) throws Exception {
        return session.get(Payment.class, Integer.valueOf(id));
    }

    @Override
    public List<Payment> getAll() throws Exception {
        String hql = "FROM Payment";
        Query<Payment> query = session.createQuery(hql, Payment.class);
        return query.list();
    }

    @Override
    public List<Payment> findByRegistrationId(String registrationId) throws Exception {
        String hql = "FROM Payment p WHERE p.registration.id = :regId ORDER BY p.paymentDate DESC";
        Query<Payment> query = session.createQuery(hql, Payment.class);
        query.setParameter("regId", Integer.valueOf(registrationId));
        return query.list();
    }

    @Override
    public List<Payment> findByStatus(Payment.PaymentStatus status) throws Exception {
        String hql = "FROM Payment p WHERE p.status = :status";
        Query<Payment> query = session.createQuery(hql, Payment.class);
        query.setParameter("status", status);
        return query.list();
    }

    @Override
    public Payment findLatestPaymentByRegistration(String registrationId) throws Exception {
        String hql = "FROM Payment p WHERE p.registration.id = :regId ORDER BY p.paymentDate DESC";
        Query<Payment> query = session.createQuery(hql, Payment.class);
        query.setParameter("regId", Integer.valueOf(registrationId));
        query.setMaxResults(1);
        List<Payment> results = query.list();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean existsByRegistrationId(String registrationId) throws Exception {
        String hql = "SELECT COUNT(p) FROM Payment p WHERE p.registration.id = :regId";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("regId", Integer.valueOf(registrationId));
        return query.uniqueResult() > 0;
    }
}
