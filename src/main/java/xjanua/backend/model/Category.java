package xjanua.backend.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Id
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "type", length = 60)
    private String type;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<CategoryChild> categoryChildren;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<TemplateCategory> templateCategories;
}