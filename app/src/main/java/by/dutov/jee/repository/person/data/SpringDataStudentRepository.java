package by.dutov.jee.repository.person.data;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface SpringDataStudentRepository extends JpaRepository<Student, Integer> {
    String SELECT_STUDENT_BY_NAME = "from Student s join s.roles r where s.userName = ?1 and r.name = 'STUDENT'";
    String SELECT_STUDENT_BY_ID = "from Student s join s.roles r where s.id = ?1 and r.name = 'STUDENT'";
    String SELECT_ALL_STUDENTS = "from Student s join s.roles r where r.name = 'STUDENT'";

    @Query(SELECT_STUDENT_BY_NAME)
    Optional<Person> find(String name);

    @Query(SELECT_STUDENT_BY_ID)
    Optional<Person> find(Integer id);

    @Query(SELECT_ALL_STUDENTS)
    List<Student> findAll();

    @Modifying
    @Query("update Student s set s.userName = ?1, s.password = ?2," +
            " s.name = ?3, s.age = ?4 where s.id = ?5")
    void update(String userName, String password,
                   String name, Integer age, Integer id);
}
