package healthClubManagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MemberClassDAO {

    private final SessionFactory sessionFactory;

    public MemberClassDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create: register a member in a class
    public void createMemberClass(MemberClass memberClass) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.save(memberClass);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get one MemberClass by id (surrogate PK)
    public MemberClass getMemberClassById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(MemberClass.class, id);
        }
    }

    // Read: all class registrations for a given member
    public List<MemberClass> getMemberClassesByMember(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Query<MemberClass> query = session.createQuery(
                    "FROM MemberClass mc WHERE mc.member = :member",
                    MemberClass.class
            );
            query.setParameter("member", member);
            return query.getResultList();
        }
    }

    // Optional: get registrations for a given class (who is in a class)
    public List<MemberClass> getMemberClassesByClass(ClassEntity gymClass) {
        try (Session session = sessionFactory.openSession()) {
            Query<MemberClass> query = session.createQuery(
                    "FROM MemberClass mc WHERE mc.gymClass = :gymClass",
                    MemberClass.class
            );
            query.setParameter("gymClass", gymClass);
            return query.getResultList();
        }
    }

    // Optional: unregister a member from a class
    public void deleteMemberClass(MemberClass memberClass) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.delete(memberClass);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
