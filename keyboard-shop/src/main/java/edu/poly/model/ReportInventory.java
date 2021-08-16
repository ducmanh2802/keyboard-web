package edu.poly.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportInventory implements Serializable{
	private Serializable group;
	private Double sum;
	private Long count;
}
