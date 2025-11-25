# models/healthmetric.py
from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from .base import Base

class HealthMetric(Base):
    __tablename__ = "healthmetric"  # matches HealthMetric table (lowercased)

    metric_id = Column(Integer, primary_key=True, autoincrement=True)
    member_id = Column(Integer, ForeignKey("member.member_id", ondelete="SET NULL"))
    metric_type = Column(String, nullable=False)
    value = Column(Integer)
    timestamp = Column(DateTime)

    member = relationship("Member", back_populates="metrics")
