package mdmihassan.form.util;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValueWrapper<T> {

    @NotNull(message = "value can't be null")
    private T value;

}
