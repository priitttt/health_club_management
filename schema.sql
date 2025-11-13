CREATE TABLE IF NOT EXISTS Member(
	member_id SERIAL PRIMARY KEY,
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL,
	email TEXT UNIQUE NOT NULL,
	date_of_birth DATE NOT NULL,
	gender TEXT,
	phone_number CHAR(10) NOT NULL,
	CONSTRAINT chk_phone CHECK(phone_number LIKE'[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]')
);
CREATE TABLE IF NOT EXISTS Trainer(
	trainer_id SERIAL PRIMARY KEY,
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL,
	email TEXT UNIQUE NOT NULL,
	speciality TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS Admin(
	admin_id SERIAL PRIMARY KEY,
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL,
	email TEXT UNIQUE NOT NULL,
	role TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS Billing(
    bill_id SERIAL PRIMARY KEY,
    member_id INTEGER REFERENCES Member(member_id) ON DELETE SET NULL,
    trainer_id INTEGER REFERENCES Trainer(trainer_id) ON DELETE SET NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_status BOOLEAN NOT NULL,
    payment_method TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS Availability(
	available_id SERIAL PRIMARY KEY,
	trainer_id INTEGER REFERENCES Trainer(trainer_id) ON DELETE SET NULL,
	Date DATE NOT NULL,
	start_time TIME NOT NULL,
	end_time TIME NOT NULL,
	status boolean DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS HealthMetric(
	metric_id SERIAL PRIMARY KEY,
	member_id INTEGER REFERENCES Member(member_id) ON DELETE SET NULL,
	metric_type TEXT NOT NULL,
	value INT,
	timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS FitnessGoal(
	goal_id SERIAL PRIMARY KEY,
	member_id INTEGER REFERENCES Member(member_id) ON DELETE SET NULL,
	goal_type TEXT NOT NULL,
	value INT,
	deadline DATE NOT NULL
);
CREATE TABLE IF NOT EXISTS Room(
	room_id SERIAL PRIMARY KEY,
	name TEXT NOT NULL,
	capacity INT NOT NULL,
	available BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS Class(
	class_id SERIAL PRIMARY KEY,
	trainer_id INTEGER REFERENCES Trainer(trainer_id) ON DELETE SET NULL,
	room_id INTEGER REFERENCES Room(room_id) ON DELETE SET NULL,
	name TEXT NOT NULL,
	capacity INT NOT NULL,
	schedule TIMESTAMP NOT NULL
);
CREATE TABLE IF NOT EXISTS PTSession(
	session_id SERIAL PRIMARY KEY,
	member_id INTEGER REFERENCES Member(member_id) ON DELETE SET NULL,
	trainer_id INTEGER REFERENCES Trainer(trainer_id) ON DELETE SET NULL,
	room_id INTEGER REFERENCES Room(room_id) ON DELETE SET NULL,
	start_time TIME NOT NULL,
	end_time TIME NOT NULL,
	status boolean DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS MemberClass (
    member_id INTEGER REFERENCES Member(member_id) ON DELETE CASCADE,
    class_id INTEGER REFERENCES Class(class_id) ON DELETE CASCADE,
    PRIMARY KEY (member_id, class_id)
);
