package ch.puzzle.marinabackend.employee;

import java.net.URI;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.puzzle.marinabackend.security.SecurityService;
import ch.puzzle.marinabackend.security.User;

@RestController
public class CurrentConfigurationResource {
    @Autowired
    private CurrentConfigurationRepository currentConfigurationRepository;
    
    @Autowired
    private EmployeeRepository employeRepository;
    
    @Autowired
    private SecurityService securityService;
    
    @GetMapping("/configuration/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource<CurrentConfiguration>> getConfigurationOfEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeRepository.findById(id);

        if (!employee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Resource<CurrentConfiguration> resource = new Resource<>(employee.get().getCurrentConfiguration());

        return ResponseEntity.ok(resource);
    }
    
    @GetMapping("/configuration/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource<CurrentConfiguration>> getMyConfiguration(Principal principal) {
        User convertPrincipal = securityService.convertPrincipal(principal);
        // find employee by email address
        Optional<Employee> employeeOptional = employeRepository.findByEmail(convertPrincipal.getEmail());
        if (!employeeOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Employee employee = employeeOptional.get();
        CurrentConfiguration currentConfiguration = employee.getCurrentConfiguration();
        if (currentConfiguration == null) {
            currentConfiguration = new CurrentConfiguration();
            currentConfiguration.setEmployee(employee);
            currentConfiguration.setWalletType(WalletType.MANUAL);
            employee.setCurrentConfiguration(currentConfiguration);
            employee = employeRepository.save(employee);
        }

        Resource<CurrentConfiguration> resource = new Resource<>(employee.getCurrentConfiguration());

        return ResponseEntity.ok(resource);
    }
    
    @DeleteMapping("/configuration/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCurrentConfiguration(@PathVariable Long id) {
        currentConfigurationRepository.deleteById(id);
    }

    @PostMapping("/configuration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createCurrentConfiguration(@RequestBody CurrentConfiguration currentConfiguration) {
        CurrentConfiguration savedCurrentConfiguration = currentConfigurationRepository.save(currentConfiguration);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedCurrentConfiguration.getId()).toUri();

        return ResponseEntity.created(location).build();

    }
    
    @PutMapping("/configuration/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> updateCurrentConfiguration(@RequestBody CurrentConfiguration currentConfiguration,
                                                             @PathVariable Long id, Principal principal) {
        Optional<CurrentConfiguration> currentConfigurationOptional = currentConfigurationRepository.findById(id);

        if (!currentConfigurationOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        CurrentConfiguration dbConfiguration = currentConfigurationOptional.get();

        User convertPrincipal = securityService.convertPrincipal(principal);
        // find employee by email address
        Optional<Employee> employeeOptional = employeRepository.findByEmail(convertPrincipal.getEmail());
        if (!employeeOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Employee dbEmployee = employeeOptional.get();
        
        // make sure the updated configuration belongs to the user
        if (dbConfiguration.getEmployee() == null ||
                !Objects.equals(dbConfiguration.getEmployee().getId(), dbEmployee.getId())) {
            return ResponseEntity.notFound().build();
        }

        currentConfiguration.setId(id);
        currentConfiguration.setEmployee(dbEmployee);
        currentConfigurationRepository.save(currentConfiguration);

        return ResponseEntity.noContent().build();
    }
}
