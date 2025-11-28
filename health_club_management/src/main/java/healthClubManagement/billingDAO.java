package healthClubManagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class BillingDAO {

    private final SessionFactory sessionFactory;

    public BillingDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create: add a billing record
    public void createBilling(Billing billing) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(billing);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Read: get bill by id
    public Billing getBillingById(Long billId) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Billing.class, billId);
        }
    }

    // Read: all bills for a member
    public List<Billing> getBillingByMember(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Query<Billing> query = session.createQuery(
                    "FROM Billing b WHERE b.member = :member",
                    Billing.class
            );
            query.setParameter("member", member);
            return query.getResultList();
        }
    }

    // Read: all bills for a trainer
    public List<Billing> getBillingByTrainer(Trainer trainer) {
        try (Session session = sessionFactory.openSession()) {
            Query<Billing> query = session.createQuery(
                    "FROM Billing b WHERE b.trainer = :trainer",
                    Billing.class
            );
            query.setParameter("trainer", trainer);
            return query.getResultList();
        }
    }

    // Read: bills in a date range
    public List<Billing> getBillingByDateRange(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Billing> query = session.createQuery(
                    "FROM Billing b WHERE b.paymentDate BETWEEN :start AND :end",
                    Billing.class
            );
            query.setParameter("start", startDate);
            query.setParameter("end", endDate);
            return query.getResultList();
        }
    }

    // Update: change status/method/amount
    public void updateBilling(Billing billing) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(billing);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Delete: remove a billing record
    public void deleteBilling(Billing billing) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(billing);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
