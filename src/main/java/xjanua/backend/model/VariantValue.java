package xjanua.backend.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
@Table(name = "variant_value")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantValue extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", referencedColumnName = "id")
    private Variant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_product_id", referencedColumnName = "id")
    private PropertyProduct propertyProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_value_id", referencedColumnName = "id")
    @Fetch(FetchMode.JOIN)
    private PropertyValue propertyValue;
}
