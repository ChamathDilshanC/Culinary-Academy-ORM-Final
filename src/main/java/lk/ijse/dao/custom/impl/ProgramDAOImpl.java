package lk.ijse.dao.custom.impl;

import lk.ijse.config.FactoryConfiguration;
import lk.ijse.dao.custom.ProgramDAO;
import lk.ijse.entity.Program;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ProgramDAOImpl implements ProgramDAO {

    @Override
    public boolean save(Program entity) throws Exception {
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
    public boolean update(Program entity) throws Exception {
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
            Program program = session.get(Program.class, id);
            if(program != null) {
                session.delete(program);
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
    public Program search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(Program.class, id);
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Program> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Program> query = session.createQuery("FROM Program", Program.class);
            return query.list();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public String getLastId() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<String> query = session.createQuery(
                    "SELECT p.programId FROM Program p ORDER BY p.programId DESC",
                    String.class);
            query.setMaxResults(1);
            String currentId = query.uniqueResult();
            return (currentId == null) ? null : currentId;
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean existsById(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Program WHERE programId = :id",
                    Long.class);
            query.setParameter("id", id);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }
}