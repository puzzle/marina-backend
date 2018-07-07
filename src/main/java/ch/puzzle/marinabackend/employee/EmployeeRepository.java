package ch.puzzle.marinabackend.employee;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    @EntityGraph(attributePaths = {"monthlyPayouts"})
    Optional<Employee> findByEmail(String email);
}
