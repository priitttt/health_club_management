# app/seed_trainers.py
from models import init_db, SessionLocal
from models.trainer import Trainer


def seed_trainers():
    init_db()
    db = SessionLocal()
    try:
        count = db.query(Trainer).count()
        print(f"Currently {count} trainer(s) in database.")

        # We want at least 3 trainers total
        trainers_to_add = []

        if count == 0:
            # none yet -> add 3
            trainers_to_add = [
                Trainer(
                    first_name="Aman",
                    last_name="Singh",
                    email="aman.trainer@example.com",
                    speciality="Strength"
                ),
                Trainer(
                    first_name="Simran",
                    last_name="Kaur",
                    email="simran.trainer@example.com",
                    speciality="Yoga"
                ),
                Trainer(
                    first_name="Raj",
                    last_name="Patel",
                    email="raj.trainer@example.com",
                    speciality="Cardio"
                ),
            ]
        elif count == 1:
            # you already have Test Trainer -> add 2 more
            trainers_to_add = [
                Trainer(
                    first_name="Simran",
                    last_name="Kaur",
                    email="simran.trainer@example.com",
                    speciality="Yoga"
                ),
                Trainer(
                    first_name="Raj",
                    last_name="Patel",
                    email="raj.trainer@example.com",
                    speciality="Cardio"
                ),
            ]
        elif count == 2:
            # add only 1 more
            trainers_to_add = [
                Trainer(
                    first_name="Raj",
                    last_name="Patel",
                    email="raj.trainer@example.com",
                    speciality="Cardio"
                ),
            ]
        else:
            print("Already have 3 or more trainers, nothing to do.")
            return

        if trainers_to_add:
            db.add_all(trainers_to_add)
            db.commit()
            print(f"Added {len(trainers_to_add)} trainer(s).")

        final_count = db.query(Trainer).count()
        print(f"Now {final_count} trainer(s) in database.")

    finally:
        db.close()


if __name__ == "__main__":
    seed_trainers()
