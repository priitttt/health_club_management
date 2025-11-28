package healthClubManagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class FitnessGoalDAO {

    private final SessionFactory sessionFactory;

    public FitnessGoalDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create: add a new fitness goal for a member
    public void createFitnessGoal(FitnessGoal fitnessGoal) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.save(fitnessGoal);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get one goal by id
    public FitnessGoal getFitnessGoalById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(FitnessGoal.class, id);
        }
    }

    // Read: all goals for a given member (for dashboard/profile)
    public List<FitnessGoal> getFitnessGoalsByMember(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Query<FitnessGoal> query = session.createQuery(
                    "FROM FitnessGoal fg WHERE fg.member = :member",
                    FitnessGoal.class
            );
            query.setParameter("member", member);
            return query.getResultList();
        }
    }

    // Optional: update an existing goal (e.g., change value/deadline)
    public void updateFitnessGoal(FitnessGoal fitnessGoal) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.update(fitnessGoal);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Optional: delete a goal
    public void deleteFitnessGoal(FitnessGoal fitnessGoal) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.delete(fitnessGoal);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
