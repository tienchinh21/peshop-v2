package xjanua.backend.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "order_code", length = 55)
    private String orderCode;

    // ================== RELATION ==================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", referencedColumnName = "id")
    private Shop shop;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    // ================== ORDER STATUS ==================
    @Column(name = "payment_method", columnDefinition = "INT UNSIGNED")
    private Integer paymentMethod;

    @Column(name = "status_payment", columnDefinition = "INT UNSIGNED")
    private Integer statusPayment;

    @Column(name = "status_order", columnDefinition = "INT UNSIGNED")
    private Integer statusOrder;

    @Column(name = "delivery_status", columnDefinition = "INT UNSIGNED")
    private Integer deliveryStatus;

    // ================== PRICING ==================
    @Column(name = "original_price", precision = 18, scale = 3)
    private BigDecimal originalPrice;

    @Column(name = "final_price", precision = 18, scale = 3)
    private BigDecimal finalPrice;

    @Column(name = "discount_price", precision = 18, scale = 3)
    private BigDecimal discountPrice;

    @Column(name = "system_voucher_discount", precision = 18, scale = 3)
    private BigDecimal systemVoucherDiscount;

    @Column(name = "shop_voucher_discount", precision = 18, scale = 3)
    private BigDecimal shopVoucherDiscount;

    @Column(name = "shipping_fee", precision = 10, scale = 2)
    private BigDecimal shippingFee;

    // ================== DELIVERY INFO ==================
    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "recipient_name", length = 60)
    private String recipientName;

    @Column(name = "recipient_phone", length = 20)
    private String recipientPhone;

    @Column(name = "note", length = 300)
    private String note;

    @Column(name = "has_flash_sale", columnDefinition = "TINYINT(1)")
    private Boolean hasFlashSale;
}
