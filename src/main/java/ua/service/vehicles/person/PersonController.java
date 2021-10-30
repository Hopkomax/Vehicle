package ua.service.vehicles.person;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {
    private final PersonRepository personRepository;
    private final JpaRepository jpaRepository;

    public PersonController(PersonRepository personRepository, @Qualifier("personRepository") JpaRepository jpaRepository) {
        this.personRepository = personRepository;
        this.jpaRepository = jpaRepository;
    }

    @GetMapping(value="/person/all")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Person> all(){
        return  personRepository.findAll();
    }

    @PostMapping(value="/person/addNew")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> addNew(@RequestBody Person person) {
        if(person.getFirstName().isEmpty()){
            return ResponseEntity.badRequest().body("First name can not be empty");
        }
        if(person.getLastName().isEmpty()){
            return ResponseEntity.badRequest().body("last Name can not be empty");
        }


        personRepository.save(person);
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping(value="/person/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(@PathVariable Long id){personRepository.deleteById(id);}
}
