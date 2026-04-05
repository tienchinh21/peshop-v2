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
@Table(name = "user_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "recipient_name", length = 60)
    private String recipientName;

    @Column(name = "street_line")
    private String streetLine;

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

    @Column(name = "full_old_address")
    private String fullOldAddress;

    @Column(name = "full_new_address")
    private String fullNewAddress;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "is_default")
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
