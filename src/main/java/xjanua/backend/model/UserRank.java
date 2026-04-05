package xjanua.backend.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_rank")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRank extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_id", referencedColumnName = "id")
    private Rank rank;

    @Column(name = "total_spent", precision = 18, scale = 2)
    private BigDecimal totalSpent;

    @Column(name = "achieved_at")
    private Instant achievedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;
}








