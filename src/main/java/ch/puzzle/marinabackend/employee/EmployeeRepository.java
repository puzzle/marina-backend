package ch.puzzle.marinabackend.employee;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    @EntityGraph(attributePaths = {"monthlyPayouts"})
    Optional<Employee> findByEmail(String email);
}
