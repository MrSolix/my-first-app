package by.dutov.jee.repository.person.data;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static by.dutov.jee.repository.ConstantsClass.GET_ALL_STUDENTS;

@Component
public interface SpringDataStudentRepository extends JpaRepository<Student, Integer> {
    String SELECT_STUDENT_BY_NAME = "from Student s where s.userName = ?1 and s.role = 'STUDENT'";
    String SELECT_STUDENT_BY_ID = "from Student s where s.id = ?1 and s.role = 'STUDENT'";

    @Query(SELECT_STUDENT_BY_NAME)
    Optional<Person> find(String name);

    @Query(SELECT_STUDENT_BY_ID)
    Optional<Person> find(Integer id);

    @Query(GET_ALL_STUDENTS)
    List<Student> findAll();

    @Modifying
    @Query("update Student s set s.userName = ?1, s.password = ?2, s.salt = ?3," +
            " s.name = ?4, s.age = ?5, s.role = ?6 where s.id = ?7")
    void update(String userName, byte[] password, byte[] salt,
                   String name, Integer age, Role role, Integer id);
}
