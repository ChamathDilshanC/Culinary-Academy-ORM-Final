package lk.ijse.dao;

import lk.ijse.dao.custom.impl.ProgramDAOImpl;
import lk.ijse.dao.custom.impl.StudentDAOImpl;
import lk.ijse.dao.custom.impl.UserDAOImpl;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {}

    public static DAOFactory getInstance() {
        return (daoFactory == null) ?
                daoFactory = new DAOFactory() :
                daoFactory;
    }

    public enum DAOTypes {
        STUDENT, USER,PROGRAM
    }

    public <T extends SuperDAO> T getDAO(DAOTypes type) {
        switch (type) {
            case STUDENT:
                return (T) new StudentDAOImpl();
            case USER:
                return (T) new UserDAOImpl();
            case PROGRAM:
                return (T) new ProgramDAOImpl();
            default:
                return null;
        }
    }
}