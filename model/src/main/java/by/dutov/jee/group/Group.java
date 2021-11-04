package by.dutov.jee.group;


import by.dutov.jee.AbstractEntity;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Group extends AbstractEntity {
    @ToString.Include
    @EqualsAndHashCode.Include
    private Teacher teacher;
    @ToString.Include
    private List<Student> students;

    {
        students = new ArrayList<>();
    }

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

    public Group withStudents(List<Student> students) {
        setStudents(students);
        for (Student s : students) {
            s.addGroup(this);
        }
        return this;
    }

    public Group addStudent(Student student) {
        if (!students.contains(student) && student != null){
            students.add(student);
        }
        return this;
    }
}
