package edu.poly.model;

import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto{
	private Long productId;
	@NotEmpty
	@Length(min = 2)
	private String name;
	@NotNull
	@Min(value = 1)
	private int quantity;
	@NotNull
	@Min(value = 10000)
	private double unitPrice;
	private String image;
	@NotEmpty
	private String desciption;
	@NotNull
	@Min(value = 0)
	@Max(value = 100)
	private double discount;
	private Date enteredDate;
	private short status;
	private Long categoryId;
	private boolean isEdit = false;
}
