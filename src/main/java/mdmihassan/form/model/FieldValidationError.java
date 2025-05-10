package mdmihassan.form.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class FieldValidationError {
    private String field;
    private Object rejectedValue;
    private String defaultMessage;
    private String code;
}
