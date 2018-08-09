package ch.puzzle.marinabackend.employee;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyPayoutRepository extends CrudRepository<MonthlyPayout, Long> {
}
