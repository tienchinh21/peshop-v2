package xjanua.backend.model;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", referencedColumnName = "id")
    private Shop shop;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "imgMain")
    private String imgMain;

    @Column(name = "status", columnDefinition = "INT UNSIGNED")
    private Integer status;

    @Column(name = "likeCount", columnDefinition = "INT UNSIGNED")
    private Integer likeCount;

    @Column(name = "viewCount", columnDefinition = "INT UNSIGNED")
    private Integer viewCount;

    @Column(name = "boughtCount", columnDefinition = "INT UNSIGNED")
    private Integer boughtCount;

    @Column(name = "reviewCount", columnDefinition = "INT UNSIGNED")
    private Integer reviewCount;

    @Column(name = "reviewPoint")
    private Float reviewPoint;

    @Column(name = "slug")
    private String slug;

    @Column(name = "reason", length = 255)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_child_id", referencedColumnName = "id")
    private CategoryChild categoryChild;

    @Column(name = "weight", columnDefinition = "INT UNSIGNED")
    private Integer weight;

    @Column(name = "height", columnDefinition = "INT UNSIGNED")
    private Integer height;

    @Column(name = "length", columnDefinition = "INT UNSIGNED")
    private Integer length;

    @Column(name = "width", columnDefinition = "INT UNSIGNED")
    private Integer width;

    @Column(name = "score")
    private Float score;

    @Column(name = "classify", columnDefinition = "INT UNSIGNED")
    private Integer classify;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Variant> variants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductInformation> productInformations;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImageProduct> images;
}
