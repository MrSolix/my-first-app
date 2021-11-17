package by.dutov.jee.people;

import by.dutov.jee.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SecondaryTables({@SecondaryTable(name = "theme", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "id")}),
        @SecondaryTable(name = "student", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "id")}), })
public class Grades extends AbstractEntity {
//    @OneToOne(cascade = CascadeType.ALL)
    @Column(table = "theme", name = "name")
    private String themeName;
    @Column(name = "grade")
    private Integer grade;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Student student;

    public Grades withId(Integer id) {
        setId(id);
        return this;
    }

    public Grades withName(String themeName) {
//        setThemeName(new Theme().withName(themeName));
        setThemeName(themeName);
        return this;
    }

    public Grades withGrades(Integer grade) {
        setGrade(grade);
        return this;
    }

}
