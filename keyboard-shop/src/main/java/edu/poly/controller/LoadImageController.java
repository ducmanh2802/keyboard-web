package edu.poly.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoadImageController {
	@RequestMapping(value = "getproduct/{photo}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ByteArrayResource> getProduct(@PathVariable("photo") String photo) {
		if (!photo.equals("") || photo != null) {
			try {
				Path filename = Paths.get("uploads/products", photo);
				byte[] buffer = Files.readAllBytes(filename);
				ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);
				return ResponseEntity.ok().contentLength(buffer.length)
						.contentType(MediaType.parseMediaType("image/png")).body(byteArrayResource);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.badRequest().build();
	}
	
	@RequestMapping(value = "getcustomer/{photo}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ByteArrayResource> getCustomer(@PathVariable("photo") String photo) {
		if (!photo.equals("") || photo != null) {
			try {
				Path filename = Paths.get("uploads/customers", photo);
				byte[] buffer = Files.readAllBytes(filename);
				ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);
				return ResponseEntity.ok().contentLength(buffer.length)
						.contentType(MediaType.parseMediaType("image/png")).body(byteArrayResource);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.badRequest().build();
	}
}
