package healthClubManagement;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "HealthMetric")
public class HealthMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metric_id")
    private Long metricId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "metric_type", nullable = false)
    private String metricType;

    @Column(name = "value")
    private Integer value;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    // Getters and setters

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HealthMetric{" +
                "metricId=" + metricId +
                ", member=" + (member != null ? member.getMemberId() : null) +
                ", metricType='" + metricType + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
