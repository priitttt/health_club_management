# app/member_menu.py

from datetime import datetime

from models import init_db, SessionLocal
from models.member import Member
from models.fitnessgoal import FitnessGoal
from models.healthmetric import HealthMetric


def select_member(db):
    """Ask the user for their email and return the Member ORM object."""
    email = input("Enter your email: ").strip()
    if not email:
        print("Email cannot be empty.")
        return None

    member = db.query(Member).filter(Member.email == email).first()
    if not member:
        print(f"No member found with email: {email}")
        return None

    print(f"\nWelcome, {member.first_name} {member.last_name}!\n")
    return member


def view_profile(member, db):
    """Show member basic info + current goal + latest health metric."""
    print("\n--- Member Profile ---")
    print(f"Member ID : {member.member_id}")
    print(f"Name      : {member.first_name} {member.last_name}")
    print(f"Email     : {member.email}")
    print(f"DOB       : {member.date_of_birth}")
    print(f"Gender    : {member.gender}")
    print(f"Phone     : {member.phone_number}")

    # nearest (earliest) goal by deadline
    goal = (
        db.query(FitnessGoal)
        .filter(FitnessGoal.member_id == member.member_id)
        .order_by(FitnessGoal.deadline.asc())
        .first()
    )

    if goal:
        print("\nCurrent goal:")
        print(
            f"  {goal.goal_type} = {goal.value} "
            f"(deadline {goal.deadline})"
        )
    else:
        print("\nCurrent goal: None set.")

    # latest metric by timestamp
    metric = (
        db.query(HealthMetric)
        .filter(HealthMetric.member_id == member.member_id)
        .order_by(HealthMetric.timestamp.desc())
        .first()
    )

    if metric:
        print("\nLatest health metric:")
        print(
            f"  {metric.metric_type} = {metric.value} "
            f"at {metric.timestamp}"
        )
    else:
        print("\nLatest health metric: None recorded.")


def update_phone(member, db):
    """Update the member phone number."""
    print("\n--- Update Phone Number ---")
    print(f"Current phone: {member.phone_number}")
    new_phone = input("Enter new 10-digit phone number: ").strip()

    if len(new_phone) != 10 or not new_phone.isdigit():
        print("Invalid phone number format. Must be 10 digits.")
        return

    member.phone_number = new_phone
    db.commit()
    db.refresh(member)
    print("Phone number updated successfully.")


def add_health_metric(member, db):
    """Add a new health metric record for the member."""
    print("\n--- Add Health Metric ---")
    metric_type = input(
        "Metric type (e.g., Weight (kg), Body Fat (%), 5km Time (min)): "
    ).strip()
    if not metric_type:
        print("Metric type cannot be empty.")
        return

    value_str = input("Metric value (integer): ").strip()
    if not value_str.isdigit():
        print("Metric value must be an integer.")
        return

    value = int(value_str)

    metric = HealthMetric(
        member_id=member.member_id,
        metric_type=metric_type,
        value=value,
        timestamp=datetime.now(),
    )

    db.add(metric)
    db.commit()
    db.refresh(metric)

    print(
        f"Added health metric: {metric.metric_type} = {metric.value} "
        f"at {metric.timestamp}"
    )


def member_menu():
    """Top-level CLI for member operations."""
    init_db()
    db = SessionLocal()
    try:
        member = select_member(db)
        if not member:
            return

        while True:
            print("\n=== Member Menu ===")
            print("1. View my profile & goals/metrics")
            print("2. Update my phone number")
            print("3. Add a new health metric")
            print("0. Exit")

            choice = input("Select option: ").strip()

            if choice == "1":
                view_profile(member, db)
            elif choice == "2":
                update_phone(member, db)
            elif choice == "3":
                add_health_metric(member, db)
            elif choice == "0":
                print("Goodbye!")
                break
            else:
                print("Invalid choice. Please try again.")

    finally:
        db.close()


if __name__ == "__main__":
    member_menu()

