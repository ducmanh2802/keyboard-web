package edu.poly.model;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto{
	private int customerId;
	@NotEmpty
	private String name;
	@NotEmpty
	@Email
	private String email;
	@NotEmpty
	@Length(min = 6)
	private String password;
	@NotEmpty
	@Pattern(regexp = "^0\\d{9}$")
	private String phone;
	@NotEmpty
	private String address;
	private boolean gender;
	private String image;
	private Date registerDate;
	private Boolean status;
	private boolean isEdit = false;
}
