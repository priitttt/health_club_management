package healthClubManagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class TrainerDAO {

    private final SessionFactory sessionFactory;

    public TrainerDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create: add a new trainer
    public void createTrainer(Trainer trainer) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(trainer);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get trainer by id
    public Trainer getTrainerById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Trainer.class, id);
        }
    }

    // Read: get trainer by email (useful if email is unique)
    public Trainer getTrainerByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Trainer> query = session.createQuery(
                    "FROM Trainer t WHERE t.email = :email",
                    Trainer.class
            );
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }

    // Read: list all trainers (for dropdowns in PT scheduling UI)
    public List<Trainer> getAllTrainers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Trainer> query = session.createQuery(
                    "FROM Trainer",
                    Trainer.class
            );
            return query.getResultList();
        }
    }

    // Update: modify trainer details
    public void updateTrainer(Trainer trainer) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(trainer);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Delete: remove trainer (optional)
    public void deleteTrainer(Trainer trainer) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(trainer);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
