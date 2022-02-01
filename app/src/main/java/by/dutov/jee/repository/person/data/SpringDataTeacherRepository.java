package by.dutov.jee.repository.person.data;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static by.dutov.jee.repository.ConstantsClass.GET_ALL_TEACHERS;

@Component
public interface SpringDataTeacherRepository extends JpaRepository<Teacher, Integer> {
    String SELECT_TEACHER_BY_NAME = "from Teacher t join t.roles r where t.userName = ?1 and r.name = 'TEACHER'";
    String SELECT_TEACHER_BY_ID = "from Teacher t join t.roles r where t.id = ?1 and r.name = 'TEACHER'";
    String SELECT_ALL_TEACHERS = "from Teacher t join t.roles r where r.name = 'TEACHER'";

    @Query(SELECT_TEACHER_BY_NAME)
    Optional<Person> find(String name);

    @Query(SELECT_TEACHER_BY_ID)
    Optional<Person> find(Integer id);

    @Query(SELECT_ALL_TEACHERS)
    List<Teacher> findAll();

    @Modifying
    @Query("update Teacher s set s.userName = ?1, s.password = ?2," +
            " s.name = ?3, s.age = ?4 where s.id = ?5")
    void update(String userName, String password,
                String name, Integer age, Integer id);
}
