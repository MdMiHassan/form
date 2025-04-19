package mdmihassan.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Builder

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "form_elements")
public class FormElement {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @ManyToOne
    private Form form;

    private String label;

    private int order;

    private boolean optional;

    @Enumerated(EnumType.STRING)
    private ElementType elementType;

    private UUID elementId;

    @OneToOne
    private FormElementScore elementScore;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private enum ElementType {
        SINGLE_LINE_TEXT,
        DATE,
        TIME,
        DATE_TIME,
        INTEGER,
        DECIMAL,
        MULTILINE_TEXT,
        SELECTION,
        DROP_DOWN,
        FILE,
        IMAGE
    }

}
