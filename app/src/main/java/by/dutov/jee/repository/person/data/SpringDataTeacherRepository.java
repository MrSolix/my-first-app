package by.dutov.jee.repository.person.data;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
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
    String SELECT_TEACHER_BY_NAME = "from Teacher t where t.userName = ?1 and t.role = 'TEACHER'";
    String SELECT_TEACHER_BY_ID = "from Teacher t where t.id = ?1 and t.role = 'TEACHER'";

    @Query(SELECT_TEACHER_BY_NAME)
    Optional<Person> find(String name);

    @Query(SELECT_TEACHER_BY_ID)
    Optional<Person> find(Integer id);

    @Query(GET_ALL_TEACHERS)
    List<Teacher> findAll();

    @Modifying
    @Query("update Teacher s set s.userName = ?1, s.password = ?2, s.salt = ?3," +
            " s.name = ?4, s.age = ?5, s.role = ?6 where s.id = ?7")
    void update(String userName, byte[] password, byte[] salt,
                String name, Integer age, Role role, Integer id);
}
