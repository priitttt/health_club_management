# models/ptsession.py
from sqlalchemy import Column, Integer, Time, Boolean, ForeignKey
from sqlalchemy.orm import relationship
from .base import Base

class PTSession(Base):
    __tablename__ = "ptsession"  # CREATE TABLE PTSession -> table name becomes 'ptsession'

    session_id = Column(Integer, primary_key=True, autoincrement=True)
    member_id = Column(Integer, ForeignKey("member.member_id", ondelete="SET NULL"))
    trainer_id = Column(Integer, ForeignKey("trainer.trainer_id", ondelete="SET NULL"))
    room_id = Column(Integer, ForeignKey("room.room_id", ondelete="SET NULL"))
    start_time = Column(Time, nullable=False)
    end_time = Column(Time, nullable=False)
    status = Column(Boolean, default=True)

    trainer = relationship("Trainer", back_populates="pt_sessions")
    member = relationship("Member", back_populates="pt_sessions")
    room = relationship("Room", back_populates="pt_sessions")
