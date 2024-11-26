package lk.ijse.dao;

import lk.ijse.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {}

    public static DAOFactory getInstance() {
        return (daoFactory == null) ?
                daoFactory = new DAOFactory() :
                daoFactory;
    }

    public enum DAOTypes {
        STUDENT, PROGRAM, REGISTRATION, PAYMENT, USER, PROGRAM_REGISTRATION
    }

    public <T extends SuperDAO> T getDAO(DAOTypes type) {
        switch (type) {
            case STUDENT:
                return (T) new StudentDAOImpl();
            case USER:
                return (T) new UserDAOImpl();
            case PROGRAM:
                return (T) new ProgramDAOImpl();
            case PAYMENT:
                return (T) new PaymentDAOImpl();
            case REGISTRATION:
                return (T) new RegistrationDAOImpl();
            case PROGRAM_REGISTRATION:
                return (T) new ProgramRegistrationDAOImpl();
            default:
                return null;
        }
    }
}