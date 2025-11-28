package healthClubManagement;

import jakarta.persistence.Entity;

@Entity
@Table(Name = "FitnessGoal")

public class FitnessGoal {
    package healthClubManagement;

import jakarta.persistence.*;
import java.time.LocalDate;

    @Entity
    @Table(name = "FitnessGoal")
    public class FitnessGoal {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "goal_id")
        private Long goalId;

        @ManyToOne
        @JoinColumn(name = "member_id")
        private Member member;

        @Column(name = "goal_type", nullable = false)
        private String goalType;

        @Column(name = "value")
        private Integer value;

        @Column(name = "deadline", nullable = false)
        private LocalDate deadline;

        public Long getGoalId() {
            return goalId;
        }

        public void setGoalId(Long goalId) {
            this.goalId = goalId;
        }

        public Member getMember() {
            return member;
        }

        public void setMember(Member member) {
            this.member = member;
        }

        public String getGoalType() {
            return goalType;
        }

        public void setGoalType(String goalType) {
            this.goalType = goalType;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public LocalDate getDeadline() {
            return deadline;
        }

        public void setDeadline(LocalDate deadline) {
            this.deadline = deadline;
        }

        @Override
        public String toString() {
            return "FitnessGoal{" +
                    "goalId=" + goalId +
                    ", member=" + (member != null ? member.getMemberId() : null) +
                    ", goalType='" + goalType + '\'' +
                    ", value=" + value +
                    ", deadline=" + deadline +
                    '}';
        }
    }

}