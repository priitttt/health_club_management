package healthClubManagement;

import jakarta.persistence.*;

@Entity
@Table(name = "MemberClass")
public class MemberClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // surrogate key just for Hibernate

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity gymClass; // name ClassEntity to avoid clash with java.lang.Class

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    }

    @Override
    public String toString() {
        return "MemberClass{" +
                "id=" + id +
                ", member=" + (member != null ? member.getMemberId() : null) +
                ", gymClass=" + (gymClass != null ? gymClass.getClassId() : null) +
                '}';
    }
}
