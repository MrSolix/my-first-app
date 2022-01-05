package by.dutov.jee.controllers.servlets.teacher;

import by.dutov.jee.controllers.servlets.AbstractJsonController;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.service.person.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/json/teachers", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TeacherJsonController implements AbstractJsonController<Teacher> {

    private final PersonService personService;

    @Override
    public List<Teacher> getAll() {
        List<Person> all = personService.findAll();
        List<Teacher> teachers = new ArrayList<>();
        for (Person person :
                all) {
            if (Role.TEACHER.equals(person.getRole())) {
                teachers.add((Teacher) person);
            }
        }
        return teachers;
    }

    @Override
    public ResponseEntity<Teacher> getEntity(int id) {
        Optional<Person> personOptional = personService.find(id);
        if (personOptional.isPresent()) {
            Person person = personOptional.get();
            if (Role.TEACHER.equals(person.getRole())) {
                return ResponseEntity.ok(((Teacher) person));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Teacher> saveEntity(Teacher teacher) {
        if (Role.TEACHER.equals(teacher.getRole())) {
            return ResponseEntity.ok((Teacher) personService.save(teacher));
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<?> updateEntity(int id, Teacher teacher) {
        if (teacher != null) {
            if (id != teacher.getId()) {
                return ResponseEntity
                        .badRequest()
                        .body("Teacher id must be equal with id in path: " + id + " != " + teacher.getId());
            }
            if (!Role.TEACHER.equals(teacher.getRole())) {
                return ResponseEntity
                        .badRequest()
                        .body("Person is not teacher");
            }
            return ResponseEntity.ok(personService.update(id, teacher));
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Teacher> deleteEntity(int id) {
        Optional<Person> person = personService.find(id);
        if (person.isPresent() && Role.TEACHER.equals(person.get().getRole())) {
            return ResponseEntity.of(Optional.of(((Teacher) personService.remove(person.get()))));
        }
        return ResponseEntity.notFound().build();
    }
}
