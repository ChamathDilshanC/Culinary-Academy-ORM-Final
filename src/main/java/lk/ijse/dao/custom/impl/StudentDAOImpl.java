package lk.ijse.dao.custom.impl;

import lk.ijse.config.FactoryConfiguration;
import lk.ijse.dao.custom.StudentDAO;
import lk.ijse.entity.Student;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public boolean save(Student entity) throws Exception {
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
    public boolean update(Student entity) throws Exception {
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
            Student student = session.get(Student.class, Integer.parseInt(id));
            if(student != null) {
                session.delete(student);
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
    public Student search(String id) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.get(Student.class, Integer.parseInt(id));
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Student> getAll() throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Student> query = session.createQuery("FROM Student", Student.class);
            return query.list();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Student searchByEmail(String email) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Student> query = session.createQuery("FROM Student WHERE email = :email", Student.class);
            query.setParameter("email", email);
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
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Student WHERE email = :email", Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Student> getStudentsByUserId(Integer userId) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Student> query = session.createQuery("FROM Student WHERE user.userId = :userId", Student.class);
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
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
                    "SELECT s.studentId FROM Student s ORDER BY s.studentId DESC",
                    Integer.class);
            query.setMaxResults(1);
            Integer currentId = query.uniqueResult();
            return (currentId == null) ? 0 : currentId;
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }
}