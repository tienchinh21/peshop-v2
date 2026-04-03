package xjanua.backend.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "template_CategoryChild")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateCategoryChild extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 45)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_child_id", referencedColumnName = "id")
    private CategoryChild categoryChild;

    @OneToMany(mappedBy = "templateCategoryChild", fetch = FetchType.LAZY)
    private List<AttributeTemplate> attributeTemplates;
}
