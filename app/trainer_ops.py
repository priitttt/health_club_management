# app/trainer_ops.py
from datetime import date, time
from sqlalchemy import select, func
from sqlalchemy.orm import Session

from models import SessionLocal
from models.trainer import Trainer
from models.availability import Availability
from models.room import Room
from models.ptsession import PTSession
from models.fitness_class import FitnessClass
from models.member import Member
from models.fitnessgoal import FitnessGoal
from models.healthmetric import HealthMetric



def set_trainer_availability(
    trainer_id: int,
    day: date,
    start: time,
    end: time,
):
    """Create a new availability slot for a trainer, preventing overlaps."""
    if end <= start:
        raise ValueError("End time must be after start time")

    db: Session = SessionLocal()
    try:
        # 1) ensure trainer exists
        trainer = db.get(Trainer, trainer_id)
        if not trainer:
            raise ValueError("Trainer not found")

        # 2) check overlap on same date
        # new slot [start, end) overlaps if:
        #   start < existing_end AND end > existing_start
        overlap_stmt = (
            select(Availability)
            .where(
                Availability.trainer_id == trainer_id,
                Availability.date == day,
                Availability.status.is_(True),
                start < Availability.end_time,
                end > Availability.start_time,
            )
        )

        if db.execute(overlap_stmt).first():
            raise ValueError("Overlapping availability slot on this date")

        # 3) insert new slot
        slot = Availability(
            trainer_id=trainer_id,
            date=day,
            start_time=start,
            end_time=end,
            status=True,
        )
        db.add(slot)
        db.commit()
        db.refresh(slot)
        return slot

    finally:
        db.close()


def get_trainer_schedule(trainer_id: int):
    """Return PT sessions and group classes for a trainer."""
    db: Session = SessionLocal()
    try:
        # PT sessions + room
        pt_sessions = (
            db.query(PTSession, Room)
            .join(Room, PTSession.room_id == Room.room_id, isouter=True)
            .filter(PTSession.trainer_id == trainer_id)
            .all()
        )

        # Classes + room
        classes = (
            db.query(FitnessClass, Room)
            .join(Room, FitnessClass.room_id == Room.room_id, isouter=True)
            .filter(FitnessClass.trainer_id == trainer_id)
            .all()
        )

        return pt_sessions, classes

    finally:
        db.close()

from models.member import Member
from models.fitnessgoal import FitnessGoal
from models.healthmetric import HealthMetric
from sqlalchemy import func

def lookup_member(search_text: str):
    """
    Search members by first name, last name, or email.
    For each match, return member + current goal + latest metric.
    """
    db: Session = SessionLocal()
    try:
        pattern = f"%{search_text}%"

        members = (
            db.query(Member)
            .filter(
                Member.first_name.ilike(pattern)
                | Member.last_name.ilike(pattern)
                | Member.email.ilike(pattern)
            )
            .all()
        )

        results = []

        for m in members:
            # closest (earliest) upcoming goal
            goal = (
                db.query(FitnessGoal)
                .filter(FitnessGoal.member_id == m.member_id)
                .order_by(FitnessGoal.deadline.asc())
                .first()
            )

            # latest health metric
            metric = (
                db.query(HealthMetric)
                .filter(HealthMetric.member_id == m.member_id)
                .order_by(HealthMetric.timestamp.desc())
                .first()
            )

            results.append({
                "member": m,
                "goal": goal,
                "metric": metric,
            })

        return results

    finally:
        db.close()
