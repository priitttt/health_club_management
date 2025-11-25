# models/room.py
from sqlalchemy import Column, Integer, String, Boolean
from sqlalchemy.orm import relationship
from .base import Base

class Room(Base):
    __tablename__ = "room"   # matches CREATE TABLE Room (...)

    room_id = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String, nullable=False)
    capacity = Column(Integer, nullable=False)
    available = Column(Boolean, default=True)

    classes = relationship("FitnessClass", back_populates="room")
    pt_sessions = relationship("PTSession", back_populates="room")
