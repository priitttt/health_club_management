package healthClubManagement.db;

import healthClubManagement.db.Member;
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
            session.persist(memberClass);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get one MemberClass by composite key (member + class)
    public MemberClass getMemberClass(Member member, healthClubManagement.db.Class gymClass) {
        try (Session session = sessionFactory.openSession()) {
            Query<MemberClass> query = session.createQuery(
                    "FROM MemberClass mc WHERE mc.member = :member AND mc.gymClass = :gymClass",
                    MemberClass.class
            );
            query.setParameter("member", member);
            query.setParameter("gymClass", gymClass);
            return query.uniqueResult();
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

    // Read: all registrations for a given class (who is in a class)
    public List<MemberClass> getMemberClassesByClass(healthClubManagement.db.Class gymClass) {
        try (Session session = sessionFactory.openSession()) {
            Query<MemberClass> query = session.createQuery(
                    "FROM MemberClass mc WHERE mc.gymClass = :gymClass",
                    MemberClass.class
            );
            query.setParameter("gymClass", gymClass);
            return query.getResultList();
        }
    }

    // Delete: unregister a member from a class using the entity
    public void deleteMemberClass(MemberClass memberClass) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(memberClass);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Convenience: unregister by member + class
    public void deleteMemberClass(Member member, Class gymClass) {
        MemberClass mc = getMemberClass(member, gymClass);
        if (mc != null) {
            deleteMemberClass(mc);
        }
    }
}
