package edu.poly.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto implements Serializable{
	private Long categoryId;
	@NotEmpty
	@Length(min = 3)
	private String name;
	
	private boolean isEdit = false;
}
