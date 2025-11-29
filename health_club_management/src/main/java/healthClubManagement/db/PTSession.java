package healthClubManagement.db;

import healthClubManagement.db.Member;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PTSession")
public class PTSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // e.g., "Scheduled", "Completed", "Cancelled"

    public PTSession() {
    }

    public PTSession(Trainer trainer,
                     Room room,
                     Member member,
                     LocalDateTime startTime,
                     LocalDateTime endTime,
                     String status) {
        this.trainer = trainer;
        this.room = room;
        this.member = member;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PTSession{" +
                "sessionId=" + sessionId +
                ", trainer=" + (trainer != null ? trainer.getTrainerId() : null) +
                ", room=" + (room != null ? room.getRoomId() : null) +
                ", member=" + (member != null ? member.getMemberId() : null) +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                '}';
    }
}
