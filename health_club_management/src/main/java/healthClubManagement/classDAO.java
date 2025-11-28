package healthClubManagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ClassDAO {

    private final SessionFactory sessionFactory;

    public ClassDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create: add a new class
    public void createClass(Class gymClass) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(gymClass);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get class by id
    public Class getClassById(Long classId) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Class.class, classId);
        }
    }

    // Read: get all classes (for listing / dropdowns)
    public List<Class> getAllClasses() {
        try (Session session = sessionFactory.openSession()) {
            Query<Class> query = session.createQuery(
                    "FROM Class",
                    Class.class
            );
            return query.getResultList();
        }
    }

    // Read: get classes taught by a specific trainer
    public List<Class> getClassesByTrainer(Trainer trainer) {
        try (Session session = sessionFactory.openSession()) {
            Query<Class> query = session.createQuery(
                    "FROM Class c WHERE c.trainer = :trainer",
                    Class.class
            );
            query.setParameter("trainer", trainer);
            return query.getResultList();
        }
    }

    // Read: get classes in a specific room
    public List<Class> getClassesByRoom(Room room) {
        try (Session session = sessionFactory.openSession()) {
            Query<Class> query = session.createQuery(
                    "FROM Class c WHERE c.room = :room",
                    Class.class
            );
            query.setParameter("room", room);
            return query.getResultList();
        }
    }

    // Update: edit class details
    public void updateClass(Class gymClass) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(gymClass);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Delete: remove a class
    public void deleteClass(Class gymClass) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(gymClass);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
