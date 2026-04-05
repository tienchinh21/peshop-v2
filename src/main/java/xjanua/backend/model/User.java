package xjanua.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "username", length = 55)
    private String username;

    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "email", length = 60)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "has_shop")
    private Boolean hasShop;

    @Column(name = "status", columnDefinition = "INT UNSIGNED")
    private Integer status;

    @Column(name = "gender", columnDefinition = "INT UNSIGNED")
    private Integer gender;

    @Column(name = "avatar", length = 255)
    private String avatar;

    // Many-to-Many relationships
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserVoucherSystem> userVoucherSystems;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserVoucherShop> userVoucherShops;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_has_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
