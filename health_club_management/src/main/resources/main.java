package healthClubManagement;

import org.hibernate.SessionFactory;

public class MainApp {

    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        MemberDAO memberDAO = new MemberDAO(sessionFactory);

        Scanner member = new Scanner(System.in);
        registerMember(scanner, memberDAO);
        scanner.close();
        HibernateUtil.shutdown();
        private static void registerMember(Scanner scanner, MemberDAO memberDAO) {
            System.out.println("=== User Registration ===");

            System.out.print("First name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Date of birth (yyyy-mm-dd): ");
            String dobInput = scanner.nextLine();
            LocalDate dob = LocalDate.parse(dobInput);

            System.out.print("Gender: ");
            String gender = scanner.nextLine();

            System.out.print("Phone number (10 digits): ");
            String phoneNumber = scanner.nextLine();

            // Optional: check if email already exists
            Member existing = memberDAO.getMemberByEmail(email);
            if (existing != null) {
                System.out.println("A member with this email already exists.");
                return;
            }

            Member member = new Member();
            member.setFirstName(firstName);
            member.setLastName(lastName);
            member.setEmail(email);
            member.setDateOfBirth(dob);
            member.setGender(gender);
            member.setPhoneNumber(phoneNumber);

            memberDAO.createMember(member);

            System.out.println("Registration successful! New member ID: " + member.getMemberId());
        }
    }
        FitnessGoalDAO fitnessGoalDAO = new FitnessGoalDAO(sessionFactory);
        HealthMetricDAO healthMetricDAO = new HealthMetricDAO(sessionFactory);
        MemberClassDAO memberClassDAO = new MemberClassDAO(sessionFactory);

        // plus TrainerDAO, ClassDAO, PTSessionDAO, etc. when you create them

        // i) User Registration
        // Member newMember = registerMember(memberDAO);

        // ii) Group Class Registration
        // registerMemberInClass(memberDAO, classDAO, memberClassDAO);

        // iii) Profile Management
        // updateMemberProfile(memberDAO);

        // iv) Dashboard
        // showDashboard(memberDAO, fitnessGoalDAO, healthMetricDAO, memberClassDAO, ptSessionDAO);

        // v) Health History
        // showHealthHistory(memberDAO, healthMetricDAO);

        // vi) PT Session scheduling
        // schedulePTSession(memberDAO, trainerDAO, ptSessionDAO, availabilityDAO, roomDAO);

        HibernateUtil.shutdown();
    }
}
