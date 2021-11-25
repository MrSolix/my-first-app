package by.dutov.jee.people.grades;

import by.dutov.jee.AbstractEntity;
import by.dutov.jee.people.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "grades")
public class Grade extends AbstractEntity {

    @ManyToOne(cascade = {CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.REFRESH})
    @JoinColumn(name = "theme_id")
    @ToString.Include
    private Theme themeName;

    @Column(name = "grade")
    private Integer grade;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Student student;

    public Grade withId(Integer id) {
        setId(id);
        return this;
    }

    public Grade withName(String themeName) {
        setThemeName(new Theme().withName(themeName));
        return this;
    }

    public Grade withGrade(Integer grade) {
        setGrade(grade);
        return this;
    }

    public String getThemeName() {
        return themeName.getName();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "theme")
    private static class Theme extends AbstractEntity implements Serializable {
        private String name;
        @OneToMany(cascade = {CascadeType.MERGE,
                CascadeType.PERSIST,
                CascadeType.DETACH,
                CascadeType.REFRESH}, mappedBy = "themeName")
        @ToString.Exclude
        private Set<Grade> grades;

        public Theme withName(String themeName) {
            setName(themeName);
            return this;
        }
    }

}
