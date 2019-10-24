package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.LocaleResolver;
import ch.puzzle.marinabackend.app.ApplicationProperties;
import ch.puzzle.marinabackend.security.SecurityService;
import ch.puzzle.marinabackend.security.User;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_PDF;

@RestController
public class EmployeeResource {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MonthlyPayoutRepository monthlyPayoutRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private MonthlyPayoutService monthlyPayoutService;

    @Autowired
    private LocaleResolver localeResolver;

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
        Employee savedEmployee = employeeRepository.findByEmail(employee.getEmail())
                .orElseGet(() -> employeeRepository.save(employee));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedEmployee.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/employees/payouts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> savePayouts(@RequestBody List<MonthlyPayoutVM> payouts) {
        List<MonthlyPayout> dbPayouts = payouts
                .stream()
                .map(p -> {
                    Employee byId = employeeRepository.findById(p.getEmployeeId())
                            .orElseThrow(() -> new RuntimeException("employee not found"));
                    MonthlyPayout payout = new MonthlyPayout();
                    payout.setEmployee(byId);
                    payout.setYear(LocalDate.now().getYear());
                    payout.setMonth(LocalDate.now().getMonthValue());
                    payout.setAmountChf(p.getAmountChf());
                    payout.setAmountBtc(p.getAmountBtc());
                    payout.setRateChf(p.getRateChf());
                    payout.setPaymentDate(LocalDateTime.now());
                    payout.setPublicAddress(p.getPublicAddress());
                    return payout;
                })
                .collect(Collectors.toList());
        monthlyPayoutRepository.saveAll(dbPayouts);
        return ResponseEntity.noContent().build();
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
        employeeOptional.map(existing -> {
            existing.setBruttoSalary(employee.getBruttoSalary());
            existing.setEmail(employee.getEmail());
            existing.setFirstName(employee.getFirstName());
            existing.setLastName(employee.getLastName());
            existing.setUsername(employee.getUsername());
            existing.setSocialSecurityNumber(employee.getSocialSecurityNumber());
            existing.setStatus(employee.getStatus());
            return existing;
        }).ifPresent(employeeRepository::save);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/employees/{id}/agreement")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> uploadAgreement(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (!employeeOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = employeeOptional.get();

        File targetFile = new File(applicationProperties.getPersistentFilePath(), employee.getUsername() + ".pdf");
        targetFile.mkdirs();
        if (targetFile.exists()) {
            targetFile.delete();
        }
        file.transferTo(targetFile);

        Agreement agreement = employee.getAgreement();
        if (agreement == null) {
            agreement = new Agreement();
        }
        agreement.setEmployee(employee);
        agreement.setAgreementPdfPath(targetFile.getAbsolutePath());
        employee.setAgreement(agreement);
        employeeRepository.save(employee);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees/user/agreement")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity downloadAgreement(Principal principal) throws IOException {
        User loggedInUser = securityService.convertPrincipal(principal);
        return getAgreement(employeeRepository.findByEmail(loggedInUser.getEmail()));
    }

    @GetMapping("/employees/{id}/agreement")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity downloadAgreement(@PathVariable Long id) throws IOException {
        return getAgreement(employeeRepository.findById(id));
    }

    @GetMapping("/employees/{id}/payouts/pdf/{year}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity getEmployeePayoutsSummaryPdf(HttpServletRequest request,
                                                       @PathVariable Long id,
                                                       @PathVariable Integer year) throws IOException, DocumentException {
        Locale locale = localeResolver.resolveLocale(request);
        byte[] payoutSummaryBytes = monthlyPayoutService.generatePayoutSummary(locale, id, year);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_PDF);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        headers.setAccessControlExposeHeaders(Collections.singletonList("Content-Disposition"));
        headers.set("Content-Disposition", "attachment; filename=" + year + ".pdf");
        return new ResponseEntity<>(payoutSummaryBytes, headers, OK);
    }

    @GetMapping("/employees/payouts/pdf/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity getAllPayoutSummariesPdf(HttpServletRequest request,
                                                   @PathVariable Integer year) throws IOException, DocumentException {
        Locale locale = localeResolver.resolveLocale(request);
        byte[] payoutSummaryBytes = monthlyPayoutService.generatePayoutSummaryForAllEmployees(locale, year);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_PDF);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        headers.setAccessControlExposeHeaders(Collections.singletonList("Content-Disposition"));
        headers.set("Content-Disposition", "attachment; filename=" + year + ".pdf");
        return new ResponseEntity<>(payoutSummaryBytes, headers, OK);
    }

    @PutMapping("/employees/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity updateStatus(@PathVariable Long id, @RequestBody String status) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (!employeeOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        EmployeeStatus parsedStatus = EmployeeStatus.valueOf(status);
        Employee employee = employeeOptional.get();
        employee.setStatus(parsedStatus);

        if (parsedStatus == EmployeeStatus.LEFT_COMPANY) {
            employee.setAgreement(null);
            if (employee.getCurrentConfiguration() != null) {
                employee.getCurrentConfiguration().setAmountChf(BigDecimal.ZERO);
                employee.getCurrentConfiguration().setBip32Node(null);
                employee.getCurrentConfiguration().setCurrentAddress(null);
                employee.getCurrentConfiguration().setCurrentAddressIndex(0L);
            }
        }

        employeeRepository.save(employee);

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity getAgreement(Optional<Employee> employeeOptional) throws IOException {
        if (!employeeOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = employeeOptional.get();
        if (employee.getAgreement() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(employee.getAgreement().getAgreementPdfPath());
        byte[] bytes = Files.readAllBytes(path);
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        headers.set("Content-Type", "application/pdf");
        headers.set("Content-Disposition", "attachment; filename=" + path.getFileName().toString());
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
