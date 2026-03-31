package xjanua.backend.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "voucher_system")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherSystem extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "code", length = 55)
    private String code;

    @Column(name = "type")
    private Integer type;

    @Column(name = "discount_value", columnDefinition = "INT UNSIGNED")
    private Integer discountValue;

    @Column(name = "maxdiscount_amount", columnDefinition = "INT UNSIGNED")
    private Integer maxDiscountAmount;

    @Column(name = "minimum_order_value", columnDefinition = "INT UNSIGNED")
    private Integer minimumOrderValue;

    @Column(name = "quantity", columnDefinition = "INT UNSIGNED")
    private Integer quantity;

    @Column(name = "quantity_used", columnDefinition = "INT UNSIGNED")
    private Integer quantityUsed;

    @Column(name = "limit_for_user", columnDefinition = "INT UNSIGNED")
    private Integer limitForUser;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "status", columnDefinition = "INT UNSIGNED")
    private Integer status;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "voucherSystem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderVoucher> orderVouchers;

    @OneToMany(mappedBy = "voucherSystem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserVoucherSystem> userVoucherSystems;
}