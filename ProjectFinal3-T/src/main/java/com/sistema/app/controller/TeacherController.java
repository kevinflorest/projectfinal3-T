package com.sistema.app.controller;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import com.sistema.app.models.Teacher;
import com.sistema.app.service.TeacherService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/api/teacher")
@RestController
public class TeacherController {

	@Autowired
	private TeacherService service;
	

	@GetMapping
	public Mono<ResponseEntity<Flux<Teacher>>> findAllTeacher(){
		return Mono.justOrEmpty(
				ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.findAllTeacher())
				);
	}
	
	
	@GetMapping("select2/{codTeacher}")
	public Mono<ResponseEntity<Teacher>> viewTeacher(@PathVariable String codTeacher){
		return service.findByCodTeacher(codTeacher).map(p-> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
			
	}
	
	@GetMapping("select/{id}")
	public Mono<ResponseEntity<Teacher>> viewTeacherId(@PathVariable String id){
		return service.findByIdTeacher(id).map(p-> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
			
	}
	
	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> saveTeacher(@Valid @RequestBody Mono<Teacher> monoTeacher){
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		return monoTeacher.flatMap(teacher -> {
			return service.saveTeacher(teacher).map(c-> {
				response.put("Teacher", c);
				response.put("mensaje", "Profesor registrado con éxito");
				response.put("timestamp", new Date());
				return ResponseEntity
					.created(URI.create("/api/teacher/".concat(c.getId())))
					.contentType(MediaType.APPLICATION_JSON)
					.body(response);
				});
			
		}).onErrorResume(r -> {
			return Mono.just(r).cast(WebExchangeBindException.class)
					.flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "El campo "+fieldError.getField() + " " + fieldError.getDefaultMessage())
					.collectList()
					.flatMap(list -> {
						response.put("errors", list);
						response.put("timestamp", new Date());
						response.put("status", HttpStatus.BAD_REQUEST.value());
						return Mono.just(ResponseEntity.badRequest().body(response));
					});		
		});
	}
	
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Teacher>> updateTeacher(@RequestBody Teacher teacher, @PathVariable String id)
	{
		return service.findByIdTeacher(id)
				.flatMap(c -> {
					c.setCodTeacher(teacher.getCodTeacher());
					return service.saveTeacher(c);
				}).map(s -> ResponseEntity.created(URI.create("/api/teacher/".concat(s.getId())))
				  .contentType(MediaType.APPLICATION_JSON)
				  .body(s))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}	
	
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteTeacher(@PathVariable String id)
	{
		return service.findByIdTeacher(id).flatMap(c -> {
			return service.deleteTeacher(c).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));		
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NO_CONTENT));
	}
	
}
