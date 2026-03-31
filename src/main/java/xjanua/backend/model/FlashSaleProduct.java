package xjanua.backend.model;

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
@Table(name = "flash_sale_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleProduct extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_id", referencedColumnName = "id")
    private FlashSale flashSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "percent_decrease", columnDefinition = "INT UNSIGNED")
    private Integer percentDecrease;

    @Column(name = "quantity", columnDefinition = "INT UNSIGNED")
    private Integer quantity;

    @Column(name = "used_quantity", columnDefinition = "INT UNSIGNED")
    private Integer usedQuantity;

    @Column(name = "order_limit", columnDefinition = "INT UNSIGNED")
    private Integer orderLimit;
}