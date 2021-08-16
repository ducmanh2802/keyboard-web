package edu.poly.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int customerId;
	
	@Column(columnDefinition = "nvarchar(100) not null")
	private String name;
	
	@Column(columnDefinition = "nvarchar(100) not null")
	private String email;
	
	@Column(length = 4000, nullable = false)
	private String password;
	
	@Column(length = 20)
	private String phone;
	
	@Column(length = 200)
	private String image;
	
	@Temporal(TemporalType.DATE)
	private Date registerDate;
	
	@Column(nullable = false)
	private Boolean status;
	
	@Column()
	private Boolean gender;
	
	@Column
	private boolean admin;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "auth_provider")
	private AuthenticationProvider authProvider;
	
	@Column(columnDefinition = "nvarchar(1000) not null")
	private String address;
	
//	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
//	private Set<Order> orders;
}
