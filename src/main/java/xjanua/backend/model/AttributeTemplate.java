package xjanua.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "attribute_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeTemplate extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 45)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_Category_id", referencedColumnName = "id")
    private TemplateCategory templateCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_CategoryChild_id", referencedColumnName = "id")
    private TemplateCategoryChild templateCategoryChild;
}
