package lk.ijse.dao.custom.impl;

import lk.ijse.config.FactoryConfiguration;
import lk.ijse.dao.custom.RegistrationDetailsDAO;
import lk.ijse.entity.RegistrationDetails;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class RegistrationDetailsDAOImpl implements RegistrationDetailsDAO {

    @Override
    public boolean save(RegistrationDetails entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error saving registration details: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(RegistrationDetails entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error updating registration details: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            RegistrationDetails details = session.get(RegistrationDetails.class, Integer.parseInt(id));
            if (details != null) {
                session.delete(details);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Error deleting registration details: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public RegistrationDetails search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(RegistrationDetails.class, Integer.parseInt(id));
        } catch (Exception e) {
            throw new RuntimeException("Error searching registration details: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<RegistrationDetails> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<RegistrationDetails> query = session.createQuery(
                    "FROM RegistrationDetails rd",
                    RegistrationDetails.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all registration details: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<RegistrationDetails> findByRegistrationId(String registrationId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<RegistrationDetails> query = session.createQuery(
                    "FROM RegistrationDetails rd WHERE rd.registration.id = :regId",
                    RegistrationDetails.class
            );
            query.setParameter("regId", Integer.parseInt(registrationId));
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding registration details by registration ID: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<RegistrationDetails> findByProgramId(String programId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<RegistrationDetails> query = session.createQuery(
                    "FROM RegistrationDetails rd WHERE rd.program.programId = :progId",
                    RegistrationDetails.class
            );
            query.setParameter("progId", programId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding registration details by program ID: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
}