package lk.ijse.bo;

import lk.ijse.bo.custom.impl.StudentBOImpl;
import lk.ijse.bo.custom.impl.UserBOImpl;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getInstance() {
        return (boFactory == null) ? boFactory = new BOFactory() : boFactory;
    }

    public enum BOTypes {
        STUDENT, USER
    }

    public SuperBO getBO(BOTypes type) {
        switch (type) {
            case STUDENT:
                return new StudentBOImpl();
            case USER:
                return new UserBOImpl();
            default:
                return null;
        }
    }
}