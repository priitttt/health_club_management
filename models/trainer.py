# models/trainer.py
from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship
from .base import Base

class Trainer(Base):
    __tablename__ = "trainer"  # matches Trainer table in schema.sql

    trainer_id = Column(Integer, primary_key=True, autoincrement=True)
    first_name = Column(String, nullable=False)
    last_name = Column(String, nullable=False)
    email = Column(String, unique=True, nullable=False)
    speciality = Column(String, nullable=False)

    # relationships for later
    availabilities = relationship("Availability", back_populates="trainer")
    pt_sessions = relationship("PTSession", back_populates="trainer")
    classes = relationship("FitnessClass", back_populates="trainer")
