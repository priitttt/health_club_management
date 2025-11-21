The health and fitness club management system provides following function,
For Member,
i)User Registration
ii) Group Class Registration
iii)Profile Management
iv) Dashboard
v) Health History
vi) PT Session scheduling
For Trainer,
i) Set Availibility
ii) Member Lookup
iii) Schedule view
For Admin,
i) Room Booking
ii) Class Management
iii) Billing & Payment

Schema has following entities,
i) Member
ii) Trainer
iii) Admin
iv) HealthMetric
v) MemberCLass
vi) FitnessGoal
vii) Class
viii) PTSession
ix) Room
xi) Billing and Payment
xii) Availibility

It follows these Relations,
Member joins Class
Members accesses HealthMetric
Members accesses FitnessGoal
Member accesses their Billing
Member joins PTSession
Trainer sets availability
Trainer trains Class
Trainer trains PTSession
Admin maintains Trainer
Admin manages Room
Admin creates Billing
Class uses Room
PTSession uses Room
