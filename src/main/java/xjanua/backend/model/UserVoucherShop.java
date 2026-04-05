package xjanua.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "user_voucher_shop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVoucherShop extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_shop_id", referencedColumnName = "id")
    private VoucherShop voucherShop;

    @Column(name = "usedCount")
    private Integer usedCount;

    @Column(name = "claimedCount")
    private Integer claimedCount;
}
