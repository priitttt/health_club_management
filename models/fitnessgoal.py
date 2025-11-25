# models/fitnessgoal.py
from sqlalchemy import Column, Integer, String, Date, ForeignKey
from sqlalchemy.orm import relationship
from .base import Base

class FitnessGoal(Base):
    __tablename__ = "fitnessgoal"  # matches FitnessGoal table (lowercased)

    goal_id = Column(Integer, primary_key=True, autoincrement=True)
    member_id = Column(Integer, ForeignKey("member.member_id", ondelete="SET NULL"))
    goal_type = Column(String, nullable=False)
    value = Column(Integer)
    deadline = Column(Date, nullable=False)

    member = relationship("Member", back_populates="goals")
