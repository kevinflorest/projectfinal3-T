package com.sistema.app.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.sistema.app.models.Teacher;
import reactor.core.publisher.Mono;

public interface TeacherDAO extends ReactiveMongoRepository<Teacher, String>{

	Mono<Teacher> findByCodTeacher(String codTeacher);
	
}
