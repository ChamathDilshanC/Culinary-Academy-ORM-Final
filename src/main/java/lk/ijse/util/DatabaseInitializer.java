package lk.ijse.util;

import lk.ijse.config.FactoryConfiguration;
import lk.ijse.entity.Program;
import lk.ijse.entity.User;
import lk.ijse.entity.User.Role;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DatabaseInitializer {
    private static final List<Program> DEFAULT_PROGRAMS = Arrays.asList(
            new Program("CA1001", "Professional Cooking", 12, 120000.00,
                    "Professional cooking training program for aspiring chefs"),
            new Program("CA1003", "Baking & Pastry Arts", 6, 60000.00,
                    "Specialized program in baking and pastry arts"),
            new Program("CA1004", "International Cuisine", 12, 100000.00,
                    "Training in various international cuisines"),
            new Program("CA1005", "Culinary Management", 12, 150000.00,
                    "Advanced program in culinary and restaurant management"),
            new Program("CA1006", "Food Safety and Hygiene", 3, 40000.00,
                    "Essential training in food safety and hygiene standards")
    );

    private static final List<User> DEFAULT_USERS = Arrays.asList(
            new User(-1, "admin",
                    PasswordEncoder.encode("admin123"),
                    "admin@culinaryacademy.com",
                    Role.ADMIN,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now())
    );

    public static void initializeDatabase() {
        initializePrograms();
        initializeUsers();
    }

    public static void initializePrograms() {
        if (InitializationTracker.isProgramsInitialized()) {
            return;
        }

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;

        try {
            Query<Long> countQuery = session.createQuery(
                    "SELECT COUNT(*) FROM Program", Long.class);
            long programCount = countQuery.uniqueResult();

            if (programCount == 0) {
                transaction = session.beginTransaction();

                for (Program program : DEFAULT_PROGRAMS) {
                    session.save(program);
                }

                transaction.commit();
                System.out.println("Default programs initialized successfully");
                InitializationTracker.markProgramsAsInitialized();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.err.println("Error initializing default programs: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public static void initializeUsers() {
        if (InitializationTracker.isUsersInitialized()) {
            return;
        }

        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;

        try {
            Query<Long> countQuery = session.createQuery(
                    "SELECT COUNT(*) FROM User", Long.class);
            long userCount = countQuery.uniqueResult();

            if (userCount == 0) {
                transaction = session.beginTransaction();

                for (User user : DEFAULT_USERS) {
                    session.save(user);
                }

                transaction.commit();
                System.out.println("Default users initialized successfully");
                InitializationTracker.markUsersAsInitialized();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.err.println("Error initializing default users: " + e.getMessage());
        } finally {
            session.close();
        }
    }
}