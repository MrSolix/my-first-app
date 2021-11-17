package by.dutov.jee.people;

import by.dutov.jee.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Theme extends AbstractEntity {
//    @OneToOne(cascade = CascadeType.ALL, mappedBy = "themeName")
//    @ToString.Exclude
//    private Grades grades;
    @Column(name = "name")
    private String name;

    public Theme withName(String name) {
        setName(name);
        return this;
    }
}
