package healthClubManagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class PTSessionDAO {

    private final SessionFactory sessionFactory;

    public PTSessionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create: schedule a new PT session
    public void createSession(PTSession sessionEntity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(sessionEntity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get session by id
    public PTSession getSessionById(Long sessionId) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(PTSession.class, sessionId);
        }
    }

    // Read: all sessions for a given member
    public List<PTSession> getSessionsByMember(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Query<PTSession> query = session.createQuery(
                    "FROM PTSession s WHERE s.member = :member",
                    PTSession.class
            );
            query.setParameter("member", member);
            return query.getResultList();
        }
    }

    // Read: all sessions for a given trainer
    public List<PTSession> getSessionsByTrainer(Trainer trainer) {
        try (Session session = sessionFactory.openSession()) {
            Query<PTSession> query = session.createQuery(
                    "FROM PTSession s WHERE s.trainer = :trainer",
                    PTSession.class
            );
            query.setParameter("trainer", trainer);
            return query.getResultList();
        }
    }

    // Read: sessions for a trainer in a time range (to check conflicts)
    public List<PTSession> getSessionsByTrainerAndTimeRange(Trainer trainer,
                                                            LocalDateTime start,
                                                            LocalDateTime end) {
        try (Session session = sessionFactory.openSession()) {
            Query<PTSession> query = session.createQuery(
                    "FROM PTSession s " +
                            "WHERE s.trainer = :trainer " +
                            "AND s.startTime < :end " +
                            "AND s.endTime > :start",
                    PTSession.class
            );
            query.setParameter("trainer", trainer);
            query.setParameter("start", start);
            query.setParameter("end", end);
            return query.getResultList();
        }
    }

    // Read: sessions for a room in a time range (room conflict check)
    public List<PTSession> getSessionsByRoomAndTimeRange(Room room,
                                                         LocalDateTime start,
                                                         LocalDateTime end) {
        try (Session session = sessionFactory.openSession()) {
            Query<PTSession> query = session.createQuery(
                    "FROM PTSession s " +
                            "WHERE s.room = :room " +
                            "AND s.startTime < :end " +
                            "AND s.endTime > :start",
                    PTSession.class
            );
            query.setParameter("room", room);
            query.setParameter("start", start);
            query.setParameter("end", end);
            return query.getResultList();
        }
    }

    // Update: change time, status, etc.
    public void updateSession(PTSession sessionEntity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(sessionEntity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Delete: cancel/remove a session
    public void deleteSession(PTSession sessionEntity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(sessionEntity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
