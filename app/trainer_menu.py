# app/trainer_menu.py
from datetime import datetime
from models import init_db, SessionLocal
from models.trainer import Trainer
from .trainer_ops import set_trainer_availability, get_trainer_schedule, lookup_member




def ensure_sample_trainer():
    db = SessionLocal()
    try:
        trainer = db.query(Trainer).first()
        if not trainer:
            trainer = Trainer(
                first_name="Test",
                last_name="Trainer",
                email="trainer@example.com",
                speciality="General"
            )
            db.add(trainer)
            db.commit()
            db.refresh(trainer)
        return trainer.trainer_id
    finally:
        db.close()


def trainer_menu():
    init_db()
    trainer_id = ensure_sample_trainer()

    while True:
        print("\n=== Trainer Menu ===")
        print("1. Add availability")
        print("2. View schedule")
        print("3. Lookup member")
        print("0. Exit")
        choice = input("Select option: ").strip()

        if choice == "1":
            day_str = input("Date (YYYY-MM-DD): ")
            start_str = input("Start time (HH:MM): ")
            end_str = input("End time (HH:MM): ")

            day = datetime.strptime(day_str, "%Y-%m-%d").date()
            start = datetime.strptime(start_str, "%H:%M").time()
            end = datetime.strptime(end_str, "%H:%M").time()

            try:
                slot = set_trainer_availability(trainer_id, day, start, end)
                print(
                    f"Added availability: {slot.date} {slot.start_time}-{slot.end_time}"
                )
            except Exception as e:
                print(f"Error: {e}")

        elif choice == "2":
            pt_sessions, classes = get_trainer_schedule(trainer_id)

            print("\n--- PT Sessions ---")
            if not pt_sessions:
                print("No PT sessions scheduled.")
            else:
                for s, room in pt_sessions:
                    room_name = room.name if room else "No room"
                    print(
                        f"Session {s.session_id}: {s.start_time}-{s.end_time} "
                        f"in room {room_name}"
                    )

            print("\n--- Classes ---")
            if not classes:
                print("No classes scheduled.")
            else:
                for c, room in classes:
                    room_name = room.name if room else "No room"
                    print(
                        f"Class {c.class_id}: {c.name} at {c.schedule} "
                        f"in room {room_name}"
                    )

        elif choice == "3":
            query = input("Enter member name or email to search: ").strip()
            matches = lookup_member(query)

            if not matches:
                print("No members found.")
            else:
                print(f"\nFound {len(matches)} member(s):")
                for item in matches:
                    m = item["member"]
                    g = item["goal"]
                    h = item["metric"]

                    print("\n-------------------------")
                    print(f"Member ID: {m.member_id}")
                    print(f"Name: {m.first_name} {m.last_name}")
                    print(f"Email: {m.email}")

                    if g:
                        print(
                            f"Goal: {g.goal_type} = {g.value} (deadline {g.deadline})"
                        )
                    else:
                        print("Goal: none set")

                    if h:
                        print(
                            f"Latest metric: {h.metric_type} = {h.value} "
                            f"at {h.timestamp}"
                        )
                    else:
                        print("Latest metric: none recorded")

        elif choice == "0":
            break
        else:
            print("Invalid choice.")






if __name__ == "__main__":
    trainer_menu()
