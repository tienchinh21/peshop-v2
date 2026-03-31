package xjanua.backend.model;

import java.util.List;

import org.hibernate.annotations.BatchSize;

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
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "property_value")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@BatchSize(size = 50)
public class PropertyValue extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "value")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_product_id", referencedColumnName = "id")
    private PropertyProduct propertyProduct;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "level", columnDefinition = "INT UNSIGNED")
    private Integer level;

    @Transient
    private Integer code;

    @OneToMany(mappedBy = "propertyValue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VariantValue> variantValues;
}