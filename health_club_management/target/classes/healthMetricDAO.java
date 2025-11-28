package healthClubManagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class HealthMetricDAO {

    private final SessionFactory sessionFactory;

    public HealthMetricDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create (log a new metric)
    public void createHealthMetric(HealthMetric healthMetric) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.save(healthMetric);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get one metric by id
    public HealthMetric getHealthMetricById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(HealthMetric.class, id);
        }
    }

    // Read: all metrics for a member (for Health History / Dashboard), newest first
    public List<HealthMetric> getHealthMetricsByMember(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Query<HealthMetric> query = session.createQuery(
                    "FROM HealthMetric hm " +
                            "WHERE hm.member = :member " +
                            "ORDER BY hm.timestamp DESC",
                    HealthMetric.class
            );
            query.setParameter("member", member);
            return query.getResultList();
        }
    }

    // Optional: delete a metric (if you support removing history entries)
    public void deleteHealthMetric(HealthMetric healthMetric) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.delete(healthMetric);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}

