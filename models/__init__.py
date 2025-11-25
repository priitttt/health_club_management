from .base import Base, engine, SessionLocal
from .trainer import Trainer
from .availability import Availability
from .room import Room
from .ptsession import PTSession
from .fitness_class import FitnessClass
from .member import Member
from .fitnessgoal import FitnessGoal
from .healthmetric import HealthMetric

def init_db():
    Base.metadata.create_all(bind=engine)
