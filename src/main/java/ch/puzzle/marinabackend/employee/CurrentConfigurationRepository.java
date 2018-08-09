package ch.puzzle.marinabackend.employee;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentConfigurationRepository extends CrudRepository<CurrentConfiguration, Long>{
}
