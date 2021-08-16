package edu.poly.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
	private Long productId;
	private String name;
	private int quantity;
	private double price;
	private Date dateAdd;
}
