# app/seed_members.py
from datetime import date, datetime
from models import init_db, SessionLocal
from models.member import Member
from models.fitnessgoal import FitnessGoal
from models.healthmetric import HealthMetric


def seed_members():
    init_db()
    db = SessionLocal()
    try:
        count = db.query(Member).count()
        print(f"Currently {count} member(s) in database.")

        members_to_add = []

        # We want at least 3 members total
        if count == 0:
            members_to_add = [
                Member(
                    first_name="Alice",
                    last_name="Singh",
                    email="alice@example.com",
                    date_of_birth=date(2004, 1, 12),
                    gender="F",
                    phone_number="1234567890",
                ),
                Member(
                    first_name="Bob",
                    last_name="Kaur",
                    email="bob@example.com",
                    date_of_birth=date(2003, 5, 20),
                    gender="M",
                    phone_number="2345678901",
                ),
                Member(
                    first_name="Charlie",
                    last_name="Patel",
                    email="charlie@example.com",
                    date_of_birth=date(2000, 9, 30),
                    gender="M",
                    phone_number="3456789012",
                ),
            ]
        elif count == 1:
            members_to_add = [
                Member(
                    first_name="Bob",
                    last_name="Kaur",
                    email="bob@example.com",
                    date_of_birth=date(2003, 5, 20),
                    gender="M",
                    phone_number="2345678901",
                ),
                Member(
                    first_name="Charlie",
                    last_name="Patel",
                    email="charlie@example.com",
                    date_of_birth=date(2000, 9, 30),
                    gender="M",
                    phone_number="3456789012",
                ),
            ]
        elif count == 2:
            members_to_add = [
                Member(
                    first_name="Charlie",
                    last_name="Patel",
                    email="charlie@example.com",
                    date_of_birth=date(2000, 9, 30),
                    gender="M",
                    phone_number="3456789012",
                ),
            ]
        else:
            print("Already have 3 or more members, nothing to do.")
            return

        if members_to_add:
            db.add_all(members_to_add)
            db.commit()
            print(f"Added {len(members_to_add)} member(s).")

            # refresh to get IDs
            for m in members_to_add:
                db.refresh(m)

            # create one goal + one metric per new member
            goals = []
            metrics = []

            for m in members_to_add:
                if m.first_name == "Alice":
                    goals.append(
                        FitnessGoal(
                            member_id=m.member_id,
                            goal_type="Weight Loss (kg)",
                            value=5,
                            deadline=date(2026, 3, 1),
                        )
                    )
                    metrics.append(
                        HealthMetric(
                            member_id=m.member_id,
                            metric_type="Weight (kg)",
                            value=70,
                            timestamp=datetime.now(),
                        )
                    )
                elif m.first_name == "Bob":
                    goals.append(
                        FitnessGoal(
                            member_id=m.member_id,
                            goal_type="Muscle Gain (kg)",
                            value=3,
                            deadline=date(2026, 4, 15),
                        )
                    )
                    metrics.append(
                        HealthMetric(
                            member_id=m.member_id,
                            metric_type="Body Fat (%)",
                            value=18,
                            timestamp=datetime.now(),
                        )
                    )
                elif m.first_name == "Charlie":
                    goals.append(
                        FitnessGoal(
                            member_id=m.member_id,
                            goal_type="Run 5km (min)",
                            value=25,
                            deadline=date(2026, 2, 28),
                        )
                    )
                    metrics.append(
                        HealthMetric(
                            member_id=m.member_id,
                            metric_type="5km Time (min)",
                            value=30,
                            timestamp=datetime.now(),
                        )
                    )

            if goals or metrics:
                db.add_all(goals + metrics)
                db.commit()
                print("Added goals and health metrics for new members.")

        final_count = db.query(Member).count()
        print(f"Now {final_count} member(s) in database.")

    finally:
        db.close()


if __name__ == "__main__":
    seed_members()
