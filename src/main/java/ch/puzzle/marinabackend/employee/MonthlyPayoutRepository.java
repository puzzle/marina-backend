package ch.puzzle.marinabackend.employee;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MonthlyPayoutRepository extends CrudRepository<MonthlyPayout, Long> {
    
    List<MonthlyPayout> findByYearOrderByMonthDesc(Integer year);
    
    List<MonthlyPayout> findByEmployeeIdAndYearOrderByMonthDesc(Long employeeId, Integer year);
}
