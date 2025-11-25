# models/availability.py
from sqlalchemy import Column, Integer, Date, Time, Boolean, ForeignKey
from sqlalchemy.orm import relationship
from .base import Base

class Availability(Base):
    __tablename__ = "availability"

    available_id = Column(Integer, primary_key=True, autoincrement=True)
    trainer_id = Column(Integer, ForeignKey("trainer.trainer_id", ondelete="SET NULL"))
    date = Column(Date, nullable=False)
    start_time = Column(Time, nullable=False)
    end_time = Column(Time, nullable=False)
    status = Column(Boolean, default=True)

    trainer = relationship("Trainer", back_populates="availabilities")
