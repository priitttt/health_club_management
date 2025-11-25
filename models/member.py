# models/member.py
from sqlalchemy import Column, Integer, String, Date, CHAR
from sqlalchemy.orm import relationship
from .base import Base

class Member(Base):
    __tablename__ = "member"   # matches CREATE TABLE Member(...)

    member_id = Column(Integer, primary_key=True, autoincrement=True)
    first_name = Column(String, nullable=False)
    last_name = Column(String, nullable=False)
    email = Column(String, unique=True, nullable=False)
    date_of_birth = Column(Date, nullable=False)
    gender = Column(String)
    phone_number = Column(CHAR(10), nullable=False)

    pt_sessions = relationship("PTSession", back_populates="member")
    goals = relationship("FitnessGoal", back_populates="member")
    metrics = relationship("HealthMetric", back_populates="member")
