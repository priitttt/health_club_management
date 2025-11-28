package healthClubManagement;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MemberClass")
@IdClass(MemberClass.MemberClassId.class)
public class MemberClass {

    @Id
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Id
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity gymClass; // maps to Class table (class_id)

    // Composite key class
    public static class MemberClassId implements Serializable {
        private Long member;
        private Long gymClass;

        public MemberClassId() {
        }

        public MemberClassId(Long member, Long gymClass) {
            this.member = member;
            this.gymClass = gymClass;
        }

        // getters/setters (optional but good practice)

        // equals/hashCode are required for composite keys
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MemberClassId)) return false;
            MemberClassId that = (MemberClassId) o;
            return java.util.Objects.equals(member, that.member) &&
                    java.util.Objects.equals(gymClass, that.gymClass);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(member, gymClass);
        }
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public ClassEntity getGymClass() {
        return gymClass;
    }

    public void setGymClass(ClassEntity gymClass) {
        this.gymClass = gymClass;
