package healthClubManagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AvailabilityDAO {

    private final SessionFactory sessionFactory;

    public AvailabilityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create: add an availability slot
    public void createAvailability(Availability availability) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.save(availability);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get by id
    public Availability getAvailabilityById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Availability.class, id);
        }
    }

    // Read: all availabilities for a trainer
    public List<Availability> getAvailabilityByTrainer(Trainer trainer) {
        try (Session session = sessionFactory.openSession()) {
            Query<Availability> query = session.createQuery(
                    "FROM Availability a WHERE a.trainer = :trainer",
                    Availability.class
            );
            query.setParameter("trainer", trainer);
            return query.getResultList();
        }
    }

    // Read: availabilities for a trainer on a specific date
    public List<Availability> getAvailabilityByTrainerAndDate(Trainer trainer, LocalDate date) {
        try (Session session = sessionFactory.openSession()) {
            Query<Availability> query = session.createQuery(
                    "FROM Availability a WHERE a.trainer = :trainer AND a.date = :date",
                    Availability.class
            );
            query.setParameter("trainer", trainer);
            query.setParameter("date", date);
            return query.getResultList();
        }
    }

    // Read: slots overlapping a time range (for conflict checks)
    public List<Availability> getAvailabilityByTrainerAndTimeRange(Trainer trainer,
                                                                   LocalDate date,
                                                                   LocalTime start,
                                                                   LocalTime end) {
        try (Session session = sessionFactory.openSession()) {
            Query<Availability> query = session.createQuery(
                    "FROM Availability a " +
                            "WHERE a.trainer = :trainer " +
                            "AND a.date = :date " +
                            "AND a.startTime < :end " +
                            "AND a.endTime > :start",
                    Availability.class
            );
            query.setParameter("trainer", trainer);
            query.setParameter("date", date);
            query.setParameter("start", start);
            query.setParameter("end", end);
            return query.getResultList();
        }
    }

    // Update: change time/status
    public void updateAvailability(Availability availability) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.update(availability);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Delete: remove a slot
    public void deleteAvailability(Availability availability) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.delete(availability);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
