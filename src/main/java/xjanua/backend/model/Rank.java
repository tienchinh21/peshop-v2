package xjanua.backend.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ranks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rank extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "min_price", precision = 18, scale = 3)
    private BigDecimal minPrice;

    @Column(name = "max_price", precision = 18, scale = 3)
    private BigDecimal maxPrice;

    @Column(name = "rank_level", columnDefinition = "TINYINT UNSIGNED")
    private Integer rankLevel;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private Boolean isActive;
}







