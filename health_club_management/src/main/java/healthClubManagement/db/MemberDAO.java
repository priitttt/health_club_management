package healthClubManagement.db;

import healthClubManagement.db.Member;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MemberDAO {

    private final SessionFactory sessionFactory;

    public MemberDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create (INSERT)
    public void createMember(Member member) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(member); // INSERT
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read (SELECT by ID)
    public Member getMemberById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Member.class, id); // SELECT by PK
        }
    }

    // Read (SELECT by email)
    public Member getMemberByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Member> query = session.createQuery(
                    "FROM Member m WHERE m.email = :email", Member.class);
            query.setParameter("email", email);
            return query.uniqueResult(); // returns null if not found
        }
    }

    // Read: get all member-class registrations
    public List<Member> getAllMembers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Member> query = session.createQuery(
                    "FROM Member",
                    Member.class
            );
            return query.getResultList();
        }
    }



    // Update
    public void updateMember(Member member) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(member); // UPDATE
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Delete
    public void deleteMember(Member member) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(member); // DELETE
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
