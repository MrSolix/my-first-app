package by.dutov.jee.group;


import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;

import java.util.List;

public class Group {
    private int numOfGroup;
    private Teacher teacher;
    private List<Student> students;

    public Group(int numOfGroup, Teacher teacher, List<Student> students) {
        this.numOfGroup = numOfGroup;
        this.teacher = teacher;
        this.students = students;
        teacher.setNumOfGroup(numOfGroup);
        for (Student s: students) {
            s.setGroupNumbers(numOfGroup);
        }
    }

    public int getNumOfGroup() {
        return numOfGroup;
    }

    public void setNumOfGroup(int numOfGroup) {
        this.numOfGroup = numOfGroup;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
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
