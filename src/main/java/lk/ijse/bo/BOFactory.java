package lk.ijse.bo;

import lk.ijse.bo.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getInstance() {
        return (boFactory == null) ? boFactory = new BOFactory() : boFactory;
    }

    public enum BOTypes {
        STUDENT, USER, PROGRAM, PAYMENT
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
            default:
                return null;
        }
    }
}