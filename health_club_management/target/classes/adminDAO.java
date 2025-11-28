package healthClubManagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class AdminDAO {

    private final SessionFactory sessionFactory;

    public AdminDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create: add an admin
    public void createAdmin(Admin admin) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.save(admin);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get admin by id
    public Admin getAdminById(Long adminId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Admin.class, adminId);
        }
    }

    // Read: get admin by email (useful for login / lookup)
    public Admin getAdminByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Admin> query = session.createQuery(
                    "FROM Admin a WHERE a.email = :email",
                    Admin.class
            );
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }

    // Read: get all admins
    public List<Admin> getAllAdmins() {
        try (Session session = sessionFactory.openSession()) {
            Query<Admin> query = session.createQuery(
                    "FROM Admin",
                    Admin.class
            );
            return query.getResultList();
        }
    }

    // Update: change name/email/role
    public void updateAdmin(Admin admin) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.update(admin);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Delete: remove an admin
    public void deleteAdmin(Admin admin) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.delete(admin);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
