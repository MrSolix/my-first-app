package by.dutov.jee.controllers.servlets.student;

import by.dutov.jee.auth.Role;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Student;
import by.dutov.jee.service.person.PersonService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(path = "/json/students", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StudentJsonController {

    private final PersonService personService;

    @GetMapping
    public List<Student> getAll() {
        List<Person> all = personService.findAll();
        List<Student> students = new ArrayList<>();
        for (Person person :
                all) {
            if (person.getRolesName(person.getRoles()).contains(Role.ROLE_STUDENT)) {
                students.add(((Student) person));
            }
        }
        return students;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable int id) {
        Optional<Person> personOptional = personService.find(id);
        if (personOptional.isPresent()) {
            Person person = personOptional.get();
            if (person.getRolesName(person.getRoles()).contains(Role.ROLE_STUDENT)) {
                return ResponseEntity.ok(((Student) person));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping()
    public ResponseEntity<Student> saveStudent(@RequestBody Student student) {
        if (student.getRolesName(student.getRoles()).contains(Role.ROLE_STUDENT)) {
            return ResponseEntity.ok((Student) personService.save(student));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable int id, @RequestBody Student student) {
        if (student != null) {
            if (id != student.getId()) {
                return ResponseEntity
                        .badRequest()
                        .body("Student id must be equal with id in path: " + id + " != " + student.getId());
            }
            if (!student.getRolesName(student.getRoles()).contains(Role.ROLE_STUDENT)) {
                return ResponseEntity
                        .badRequest()
                        .body("Person is not student");
            }
            return ResponseEntity.ok(personService.update(id, student));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable int id) {
        Optional<Person> optionalPerson = personService.find(id);
        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            if (person.getRolesName(person.getRoles()).contains(Role.ROLE_STUDENT))
                return ResponseEntity.of(Optional.of(((Student) personService.remove(person))));
        }
        return ResponseEntity.notFound().build();
    }
}
