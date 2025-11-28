package healthClubManagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RoomDAO {

    private final SessionFactory sessionFactory;

    public RoomDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create: add a new room
    public void createRoom(Room room) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(room);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get room by id
    public Room getRoomById(Long roomId) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Room.class, roomId);
        }
    }

    // Read: get all rooms
    public List<Room> getAllRooms() {
        try (Session session = sessionFactory.openSession()) {
            Query<Room> query = session.createQuery(
                    "FROM Room",
                    Room.class
            );
            return query.getResultList();
        }
    }

    // Read: get only available rooms
    public List<Room> getAvailableRooms() {
        try (Session session = sessionFactory.openSession()) {
            Query<Room> query = session.createQuery(
                    "FROM Room r WHERE r.available = true",
                    Room.class
            );
            return query.getResultList();
        }
    }

    // Update: change name/capacity/available
    public void updateRoom(Room room) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(room);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Delete: remove a room
    public void deleteRoom(Room room) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(room);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
