package xjanua.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "status", columnDefinition = "INT UNSIGNED")
    private Integer status;

    @Column(name = "old_provice_id", length = 15)
    private String oldProviceId;

    @Column(name = "old_district_id", length = 15)
    private String oldDistrictId;

    @Column(name = "old_ward_id", length = 15)
    private String oldWardId;

    @Column(name = "new_provice_id", length = 15)
    private String newProviceId;

    @Column(name = "new_ward_id", length = 15)
    private String newWardId;

    @Column(name = "street_line")
    private String streetLine;

    @Column(name = "full_old_address", length = 500)
    private String fullOldAddress;

    @Column(name = "full_new_address", length = 500)
    private String fullNewAddress;

    @Column(name = "prd_count", columnDefinition = "INT UNSIGNED")
    private Integer prdCount;

    @Column(name = "followers_count", columnDefinition = "INT UNSIGNED")
    private Integer followersCount;

    @Column(name = "following_count", columnDefinition = "INT UNSIGNED")
    private Integer followingCount;

    @Column(name = "ghn_id", columnDefinition = "INT UNSIGNED")
    private Integer ghnId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;
}