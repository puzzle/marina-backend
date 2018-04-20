package ch.puzzle.marinabackend.employe;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class EmployeResource {

	@Autowired
	private EmployeRepository employeRepository;

	@GetMapping("/employees")
	public Iterable<Employe> getEmployees() {
		return employeRepository.findAll();
	}

	@GetMapping("/employees/{id}")
	public ResponseEntity<Resource<Employe>> getEmploye(@PathVariable Long id) {
		Optional<Employe> employe = employeRepository.findById(id);

		if (!employe.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Resource<Employe> resource = new Resource<Employe>(employe.get());

		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getEmployees());
		resource.add(linkTo.withRel("all-employees"));

		return ResponseEntity.ok(resource);
	}

	@DeleteMapping("/employees/{id}")
	public void deleteEmploye(@PathVariable Long id) {
		employeRepository.deleteById(id);
	}

	@PostMapping("/employees")
	public ResponseEntity<Object> createEmploye(@RequestBody Employe employe) {
		Employe savedEmploye = employeRepository.save(employe);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedEmploye.getId()).toUri();

		return ResponseEntity.created(location).build();

	}

	@PutMapping("/employees/{id}")
	public ResponseEntity<Object> updateEmploye(@RequestBody Employe employe, @PathVariable Long id) {
		Optional<Employe> employeeOptional = employeRepository.findById(id);

		if (!employeeOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		employe.setId(id);
		employeRepository.save(employe);

		return ResponseEntity.noContent().build();
	}

}
