package by.dutov.jee.controllers.servlets;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AbstractJsonController<T> {

    @GetMapping
    List<T> getAll();

    @GetMapping("/{id}")
    ResponseEntity<T> getEntity(@PathVariable int id);

    @PostMapping()
    ResponseEntity<T> saveEntity(@RequestBody T t);

    @PutMapping("/{id}")
    ResponseEntity<?> updateEntity(@PathVariable int id, @RequestBody T t);

    @DeleteMapping("/{id}")
    ResponseEntity<T> deleteEntity(@PathVariable int id);
}
