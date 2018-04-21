package ch.puzzle.marinabackend.employee;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.puzzle.marinabackend.security.User;

@RestController
public class EmployeeResource {

    @Autowired
    private EmployeeRepository employeRepository;

    @GetMapping("/employees")
    public Iterable<Employee> getEmployees() {
        return employeRepository.findAll();
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Resource<Employee>> getEmploye(@PathVariable Long id) {
        Optional<Employee> employe = employeRepository.findById(id);

        if (!employe.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Resource<Employee> resource = new Resource<Employee>(employe.get());

        ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getEmployees());
        resource.add(linkTo.withRel("all-employees"));

        return ResponseEntity.ok(resource);
    }
    @GetMapping("/employees/email")
    public ResponseEntity<Resource<Employee>> getEmployeeByEmail(@Param("email") String email) {
        Optional<Employee> employe = employeRepository.findByEmail(email);

        if (!employe.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Resource<Employee> resource = new Resource<Employee>(employe.get());

        ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getEmployees());
        resource.add(linkTo.withRel("all-employees"));

        return ResponseEntity.ok(resource);
    } 

    @DeleteMapping("/employees/{id}")
    public void deleteEmploye(@PathVariable Long id) {
        employeRepository.deleteById(id);
    }

    @PostMapping("/employees")
    public ResponseEntity<Object> createEmploye(@RequestBody Employee employe) {
        Employee savedEmploye = employeRepository.save(employe);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedEmploye.getId()).toUri();

        return ResponseEntity.created(location).build();

    }
    @PostMapping("/employees/user")
    public ResponseEntity<Object> createEmployeByPrincipal(Principal principal) {
        OAuth2Authentication auth = (OAuth2Authentication) principal;
        Employee savedEmploye = employeRepository.save(new Employee(new User(auth)));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedEmploye.getId()).toUri();

        return ResponseEntity.created(location).build();

    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Object> updateEmploye(@RequestBody Employee employe, @PathVariable Long id) {
        Optional<Employee> employeeOptional = employeRepository.findById(id);

        if (!employeeOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        employe.setId(id);
        employeRepository.save(employe);

        return ResponseEntity.noContent().build();
    }

}
