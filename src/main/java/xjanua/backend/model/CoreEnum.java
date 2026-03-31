package xjanua.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "core_enum")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreEnum extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;
}
