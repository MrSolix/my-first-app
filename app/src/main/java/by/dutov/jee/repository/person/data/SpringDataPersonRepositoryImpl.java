package by.dutov.jee.repository.person.data;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.ConstantsClass;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("dataPerson")
@RequiredArgsConstructor
public class SpringDataPersonRepositoryImpl implements PersonDAOInterface {

    private final SpringDataStudentRepository springDataStudentRepository;
    private final SpringDataTeacherRepository springDataTeacherRepository;

    @Override
    public Optional<Person> find(String name) {
        Optional<Person> student = springDataStudentRepository.find(name);
        if (student.isPresent()) {
            return student;
        }
        return springDataTeacherRepository.find(name);
    }

    @Override
    public Person save(Person person) {
        if (Role.STUDENT.equals(person.getRole())){
            if (person.getId() != null) {
                    springDataStudentRepository.update(person.getUserName(), person.getPassword(), person.getSalt(),
                            person.getName(), person.getAge(), person.getRole(), person.getId());
                    return person;
            }
            return springDataStudentRepository.saveAndFlush(((Student) person));
        }
        if (Role.TEACHER.equals(person.getRole())) {
            if (person.getId() != null) {
                springDataTeacherRepository.update(person.getUserName(), person.getPassword(), person.getSalt(),
                        person.getName(), person.getAge(), person.getRole(), person.getId());
                return person;
            }
            return springDataTeacherRepository.saveAndFlush(((Teacher) person));
        }
        throw new DataBaseException(ConstantsClass.ERROR_FROM_SAVE);
    }

    @Override
    public Optional<Person> find(Integer id) {
        Optional<Person> student = springDataStudentRepository.find(id);
        if (student.isPresent()) {
            return student;
        }
        return springDataTeacherRepository.find(id);
    }

    @Override
    public Person update(Integer id, Person person) {
        person.setId(id);
        if (Role.STUDENT.equals(person.getRole())) {
            return springDataStudentRepository.save(((Student) person));
        }
        return springDataTeacherRepository.save(((Teacher) person));
    }

    @Override
    public Person remove(Person person) {
        if (Role.STUDENT.equals(person.getRole())) {
            springDataStudentRepository.deleteById(person.getId());
        }
        if (Role.TEACHER.equals(person.getRole())) {
            springDataTeacherRepository.deleteById(person.getId());
        }
        return person;
    }

    @Override
    public List<Person> findAll() {
        List<Person> result = new ArrayList<>();
        result.addAll(springDataStudentRepository.findAll());
        result.addAll(springDataTeacherRepository.findAll());
        return result;
    }
}
