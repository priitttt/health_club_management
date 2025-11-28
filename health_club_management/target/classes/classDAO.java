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
    public void createClass(ClassEntity gymClass) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.save(gymClass);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get class by id
    public ClassEntity getClassById(Long classId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(ClassEntity.class, classId);
        }
    }

    // Read: get all classes (for listing / dropdowns)
    public List<ClassEntity> getAllClasses() {
        try (Session session = sessionFactory.openSession()) {
            Query<ClassEntity> query = session.createQuery(
                    "FROM ClassEntity",
                    ClassEntity.class
            );
            return query.getResultList();
        }
    }

    // Read: get classes taught by a specific trainer
    public List<ClassEntity> getClassesByTrainer(Trainer trainer) {
        try (Session session = sessionFactory.openSession()) {
            Query<ClassEntity> query = session.createQuery(
                    "FROM ClassEntity c WHERE c.trainer = :trainer",
                    ClassEntity.class
            );
            query.setParameter("trainer", trainer);
            return query.getResultList();
        }
    }

    // Read: get classes in a specific room
    public List<ClassEntity> getClassesByRoom(Room room) {
        try (Session session = sessionFactory.openSession()) {
            Query<ClassEntity> query = session.createQuery(
                    "FROM ClassEntity c WHERE c.room = :room",
                    ClassEntity.class
            );
            query.setParameter("room", room);
            return query.getResultList();
        }
    }

    // Update: edit class details
    public void updateClass(ClassEntity gymClass) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.update(gymClass);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Delete: remove a class
    public void deleteClass(ClassEntity gymClass) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.delete(gymClass);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
