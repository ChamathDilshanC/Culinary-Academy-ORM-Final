package lk.ijse.bo;

import lk.ijse.bo.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getInstance() {
        return (boFactory == null) ? boFactory = new BOFactory() : boFactory;
    }

    public enum BOTypes {
        STUDENT, PROGRAM, REGISTRATION, PAYMENT, USER, PROGRAM_REGISTRATION
    }

    public SuperBO getBO(BOTypes type) {
        switch (type) {
            case STUDENT:
                return new StudentBOImpl();
            case USER:
                return new UserBOImpl();
            case PROGRAM:
                return new ProgramBOImpl();
            case PAYMENT:
                return new PaymentBOImpl();
            case REGISTRATION:
                return new RegistrationBOImpl();
            case PROGRAM_REGISTRATION:
                return new ProgramRegistrationBOImpl();
            default:
                return null;
        }
    }
}