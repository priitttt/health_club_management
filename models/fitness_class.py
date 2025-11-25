# models/fitness_class.py
from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from .base import Base

class FitnessClass(Base):
    __tablename__ = "class"  # matches CREATE TABLE Class (...)

    class_id = Column(Integer, primary_key=True, autoincrement=True)
    trainer_id = Column(Integer, ForeignKey("trainer.trainer_id", ondelete="SET NULL"))
    room_id = Column(Integer, ForeignKey("room.room_id", ondelete="SET NULL"))
    name = Column(String, nullable=False)
    capacity = Column(Integer, nullable=False)
    schedule = Column(DateTime, nullable=False)

    trainer = relationship("Trainer", back_populates="classes")
    room = relationship("Room", back_populates="classes")
