package com.spring_boot_Restapi_CURD.Simple.Program;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/student")
public class StudentController {
	@Autowired
	StudentRepository repo;
	@GetMapping("/student")
	
	public List<Student> getAllStudent(){
		return repo.findAll();
	}
	@GetMapping("/{id}")
	public ResponseEntity<Student>getStudent(@PathVariable int id){
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	@PostMapping("/add")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void createStudent(@RequestBody Student s) {
		repo.save(s);
	}
	@PutMapping("/update/{id}")
	public ResponseEntity<Student> updateStudent(@PathVariable int id, @RequestBody Student	updatedStudent) {
		return repo.findById(id)
				.map(existingStudent -> {
					existingStudent.setName(updatedStudent.getName());
					existingStudent.setCourse(updatedStudent.getCourse());
					existingStudent.setAge(updatedStudent.getAge());
					Student savedStudent = repo.save(existingStudent);
					return ResponseEntity.ok(savedStudent);
				})
				.orElse(ResponseEntity.notFound().build());
	}
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Student>removeStudent(@PathVariable int id){
		if(!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		repo.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	//Pagination
	@GetMapping("/{pNumber}/{pSize}")
	public List<Student>getPaginated(@PathVariable int pNumber, @PathVariable int pSize) {
		Pageable p = PageRequest.of(pNumber, pSize);
		Page<Student> pageRes = repo.findAll(p);
		return pageRes.hasContent()?pageRes.getContent():List.of();
	}
	@GetMapping("/sorting")
	public List<Student>getAllBycols(@RequestParam String f, @RequestParam(defaultValue = "asc") String d) {
		Sort.Direction sd = d.equalsIgnoreCase("desc") ? Sort.Direction.DESC:Sort.Direction.ASC;
		return repo.findAll(Sort.by(sd, f));
	}
	@GetMapping("/{pNumber}/{pSize}/{sorting}")
	public List<Student>getPaginatedAndSorted(@PathVariable int pNumber, @PathVariable int pSize, @RequestParam String sortF, 
			@RequestParam (defaultValue = "asc") String sortD) 
	{
		Sort s = sortD.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortF).ascending():Sort.by(sortF).descending();
		Pageable p = PageRequest.of(pNumber, pSize, s);
		Page<Student> pageRes =  repo.findAll(p);
		return pageRes.hasContent() ? pageRes.getContent():List.of();
	}
}