package com.sistema.app.models;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="teacher")
public class Teacher {

	@Id
	private String id;
	
	@NotEmpty
	private String codTeacher;
	
}
