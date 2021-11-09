package by.dutov.jee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractEntity {
    private Integer id;

    public AbstractEntity withId(Integer id) {
        setId(id);
        return this;
    }
}
