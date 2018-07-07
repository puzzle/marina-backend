package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.security.SecurityService;
import ch.puzzle.marinabackend.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class EmployeeResource {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SecurityService securityService;

    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource<Employee>> getEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);

        if (!employee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Resource<Employee> resource = new Resource<>(employee.get());

        ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getEmployees());
        resource.add(linkTo.withRel("all-employees"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/employees/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource<Employee>> getEmployeeByEmail(@Param("email") String email) {
        Optional<Employee> employee = employeeRepository.findByEmail(email);

        if (!employee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Resource<Employee> resource = new Resource<>(employee.get());

        ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getEmployees());
        resource.add(linkTo.withRel("all-employees"));

        return ResponseEntity.ok(resource);
    }
    
    @GetMapping("/employees/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource<Employee>> getEmployeeByLoggedInUser(Principal principal) {
        User loggedInUser = securityService.convertPrincipal(principal);
        return getEmployeeByEmail(loggedInUser.getEmail());
    }

    @DeleteMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
    }

    @PostMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedEmployee.getId()).toUri();

        return ResponseEntity.created(location).build();

    }

    @PostMapping("/employees/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createEmployeeByPrincipal(Principal principal) {
        User convertedPrincipal = securityService.convertPrincipal(principal);
        if (convertedPrincipal == null) {
            return ResponseEntity.notFound().build();
        }

        Employee savedEmployee = employeeRepository.save(new Employee(convertedPrincipal));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/employees/{id}")
                .buildAndExpand(savedEmployee.getId()).toUri();

        return ResponseEntity.created(location).build();

    }

    @PutMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateEmployee(@RequestBody Employee employee, @PathVariable Long id) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (!employeeOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        employee.setId(id);
        employeeRepository.save(employee);

        return ResponseEntity.noContent().build();
    }

}
