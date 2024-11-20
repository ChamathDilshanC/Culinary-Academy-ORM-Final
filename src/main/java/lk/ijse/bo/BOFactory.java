package lk.ijse.bo;

import lk.ijse.bo.custom.impl.ProgramBOImpl;
import lk.ijse.bo.custom.impl.StudentBOImpl;
import lk.ijse.bo.custom.impl.UserBOImpl;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getInstance() {
        return (boFactory == null) ? boFactory = new BOFactory() : boFactory;
    }

    public enum BOTypes {
        STUDENT, USER,PROGRAM
    }

    public SuperBO getBO(BOTypes type) {
        switch (type) {
            case STUDENT:
                return new StudentBOImpl();
            case USER:
                return new UserBOImpl();
            case PROGRAM:
                return new ProgramBOImpl();
            default:
                return null;
        }
    }
}