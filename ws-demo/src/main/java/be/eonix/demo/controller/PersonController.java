package be.eonix.demo.controller;

import be.eonix.demo.domain.Person;
import be.eonix.demo.repository.PersonRepository;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @GetMapping
    public ResponseEntity<List<Person>> getPersons(@RequestParam(required = false) String nom,
                                                   @RequestParam(required = false) String prenom) {
        if (StringUtils.isNoneBlank(nom, prenom)) {
            return ResponseEntity.ok(personRepository.findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(nom, prenom));
        } else if (StringUtils.isNotBlank(nom)) {
            return ResponseEntity.ok(personRepository.findByNomContainingIgnoreCase(nom));
        } else if (StringUtils.isNotBlank(prenom)) {
            return ResponseEntity.ok(personRepository.findByPrenomContainingIgnoreCase(prenom));
        } else {
            return ResponseEntity.ok(personRepository.findAll());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable UUID id) {
        Optional<Person> personOpt = personRepository.findById(id);
        return personOpt.isPresent() ? ResponseEntity.ok(personOpt.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Person> addPerson(@Valid @RequestBody Person person) {
        person.setId(null);
        Person savedPerson = personRepository.save(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable UUID id, @Valid @RequestBody Person updatedPerson) {
        Optional<Person> personOpt = personRepository.findById(id);
        Person existingPerson = personOpt.orElse(null);
        if (personOpt.isPresent()) {
            existingPerson.setNom(updatedPerson.getNom());
            existingPerson.setPrenom(updatedPerson.getPrenom());
            return ResponseEntity.ok(personRepository.save(existingPerson));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable UUID id) {
        personRepository.deleteById(id);
    }
}