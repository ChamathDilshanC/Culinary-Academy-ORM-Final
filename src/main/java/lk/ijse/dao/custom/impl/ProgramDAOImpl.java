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
            throw new RuntimeException("Error saving program: " + e.getMessage(), e);
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
            throw new RuntimeException("Error updating program: " + e.getMessage(), e);
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
            throw new RuntimeException("Error deleting program: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public Program search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Program program = session.get(Program.class, id);
            if(program == null) {
                throw new RuntimeException("Program not found with id: " + id);
            }
            return program;
        } catch (Exception e) {
            throw new RuntimeException("Error searching program: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Program> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Program> query = session.createQuery("FROM Program p", Program.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all programs: " + e.getMessage(), e);
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
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error getting last program id: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean existsById(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(p) FROM Program p WHERE p.programId = :id",
                    Long.class);
            query.setParameter("id", id);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking program existence: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
}