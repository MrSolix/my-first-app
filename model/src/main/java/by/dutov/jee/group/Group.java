package by.dutov.jee.group;


import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Group {
    private int numOfGroup;
    private Teacher teacher;
    private List<Student> students;

    public Group withNumOfGroup(int numOfGroup) {
        setNumOfGroup(numOfGroup);
        return this;
    }

    public Group withTeacher(Teacher teacher) {
        setTeacher(teacher);
        teacher.setGroup(this);
        return this;
    }

    public Group withStudents(List<Student> students) {
        setStudents(students);
        for (Student s : students) {
            s.addGroups(this);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return numOfGroup == group.numOfGroup &&
                Objects.equals(teacher, group.teacher) &&
                Objects.equals(students, group.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numOfGroup, teacher, students);
    }

    @Override
    public String toString() {
        return "Group{" +
                "numOfGroup=" + numOfGroup +
                ", teacher=" + teacher +
                ", students=" + students +
                '}';
    }
}
