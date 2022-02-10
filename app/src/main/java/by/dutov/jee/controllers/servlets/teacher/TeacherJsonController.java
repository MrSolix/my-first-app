package by.dutov.jee.controllers.servlets.teacher;

import by.dutov.jee.auth.Role;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.service.person.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/json/teachers", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class TeacherJsonController {

    private final PersonService personService;

    @GetMapping
    public List<Teacher> getAll() {
        List<Person> all = personService.findAll();
        List<Teacher> teachers = new ArrayList<>();
        for (Person person :
                all) {
            if (person.getRolesName(person.getRoles()).contains(Role.ROLE_TEACHER)) {
                teachers.add((Teacher) person);
            }
        }
        return teachers;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacher(@PathVariable int id) {
        Optional<Person> personOptional = personService.find(id);
        if (personOptional.isPresent()) {
            Person person = personOptional.get();
            if (person.getRolesName(person.getRoles()).contains(Role.ROLE_TEACHER)) {
                return ResponseEntity.ok(((Teacher) person));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Teacher> saveTeacher(@RequestBody Teacher teacher) {
        if (teacher.getRolesName(teacher.getRoles()).contains(Role.ROLE_TEACHER)) {
            return ResponseEntity.ok((Teacher) personService.save(teacher));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeacher(@PathVariable int id, @RequestBody Teacher teacher) {
        if (teacher != null) {
            if (id != teacher.getId()) {
                return ResponseEntity
                        .badRequest()
                        .body("Teacher id must be equal with id in path: " + id + " != " + teacher.getId());
            }
            if (!teacher.getRolesName(teacher.getRoles()).contains(Role.ROLE_TEACHER)) {
                return ResponseEntity
                        .badRequest()
                        .body("Person is not teacher");
            }
            return ResponseEntity.ok(personService.update(id, teacher));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Teacher> deleteTeacher(@PathVariable int id) {
        Optional<Person> optionalPerson = personService.find(id);
        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            if (person.getRolesName(person.getRoles()).contains(Role.ROLE_TEACHER))
                return ResponseEntity.of(Optional.of(((Teacher) personService.remove(person))));
        }
        return ResponseEntity.notFound().build();
    }
}
