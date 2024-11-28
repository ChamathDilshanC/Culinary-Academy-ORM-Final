package lk.ijse.dao.custom.impl;

import lk.ijse.config.FactoryConfiguration;
import lk.ijse.dao.custom.PaymentDAO;
import lk.ijse.entity.Payment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public boolean save(Payment entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            entity.setBalance(entity.getTotalAmount() - entity.getAmount());
            session.save(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error saving payment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
    @Override
    public boolean update(Payment entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error updating payment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Payment payment = session.get(Payment.class, Integer.parseInt(id));
            if (payment != null) {
                session.delete(payment);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error deleting payment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public Payment search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(Payment.class, Integer.parseInt(id));
        } catch (Exception e) {
            throw new RuntimeException("Error searching payment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Payment> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Payment> query = session.createQuery("FROM Payment p", Payment.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all payments: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public String getLastId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Integer> query = session.createQuery(
                    "SELECT p.id FROM Payment p ORDER BY p.id DESC",
                    Integer.class
            );
            query.setMaxResults(1);
            Integer lastId = query.uniqueResult();
            return lastId != null ? lastId.toString() : null;
        } catch (Exception e) {
            throw new RuntimeException("Error getting last payment id: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Payment> findByRegistrationId(String registrationId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Payment> query = session.createQuery(
                    "FROM Payment p WHERE p.registration.id = :regId",
                    Payment.class
            );
            query.setParameter("regId", Integer.parseInt(registrationId));
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding payments by registration ID: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public double getTotalPaymentsByRegistrationId(String registrationId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Object[]> query = session.createQuery(
                    "SELECT SUM(p.amount), p.totalAmount FROM Payment p " +
                            "WHERE p.registration.id = :regId " +
                            "GROUP BY p.totalAmount",
                    Object[].class
            );
            query.setParameter("regId", Integer.parseInt(registrationId));

            Object[] result = query.uniqueResult();
            if (result != null) {
                Double totalPaid = (Double) result[0];
                Double totalAmount = (Double) result[1];
                return totalAmount - (totalPaid != null ? totalPaid : 0.0);
            }
            return 0.0;
        } catch (Exception e) {
            throw new RuntimeException("Error calculating balance: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
}