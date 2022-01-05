package by.dutov.jee.group;


import by.dutov.jee.AbstractEntity;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "\"group\"")
public class Group extends AbstractEntity {
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JsonIgnore
    private Teacher teacher;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(
            name = "group_student",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    @JsonIgnore
    private Set<Student> students = new HashSet<>();


    public Group withId(Integer id) {
        setId(id);
        return this;
    }

    public Group withTeacher(Teacher teacher) {
        setTeacher(teacher);
        if (teacher != null) {
            teacher.setGroup(this);
        }
        return this;
    }

    public Group withStudents(Set<Student> students) {
        setStudents(students);
        for (Student s : students) {
            s.addGroup(this);
        }
        return this;
    }

    public Group addStudent(Student student) {
        if (!students.contains(student) && student != null) {
            students.add(student);
        }
        return this;
    }

    public void removeStudent(Student student) {
        students.remove(student);
        student.getGroups().remove(this);
    }

    public void removeTeacher(Teacher teacher) {
        this.teacher = null;
        teacher.setGroup(null);
    }
}
