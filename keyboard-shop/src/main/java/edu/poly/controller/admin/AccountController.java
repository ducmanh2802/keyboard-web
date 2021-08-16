package edu.poly.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.Customer;
import edu.poly.model.CustomerDto;
import edu.poly.repository.CustomerRepository;

@Controller
@RequestMapping("/admin/account")
public class AccountController {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@RequestMapping("")
	public ModelAndView info(ModelMap model, Principal principal) {
		Customer c = customerRepository.FindByEmail(principal.getName()).get();

		model.addAttribute("user", c);
		return new ModelAndView("/admin/infomation", model);
	}

	@GetMapping("/editProfile")
	public ModelAndView editForm(ModelMap model, Principal principal) {

		model.addAttribute("customer", customerRepository.FindByEmail(principal.getName()).get());

		return new ModelAndView("/admin/editProfile", model);
	}

	@PostMapping("/editProfile")
	public ModelAndView edit(ModelMap model, @Valid @ModelAttribute("customer") CustomerDto dto, BindingResult result,
			@RequestParam("photo") MultipartFile photo, Principal principal) throws IOException {
		if (result.hasErrors()) {
			return new ModelAndView("/admin/account", model);
		}
		Customer c = customerRepository.FindByEmail(principal.getName()).get();
		if (!photo.getOriginalFilename().equals("")) {
			upload(photo, "uploads/customers");
			c.setImage(photo.getOriginalFilename());
		}
		c.setName(dto.getName());
		c.setGender(dto.isGender());
		c.setPhone(dto.getPhone());
		c.setAddress(dto.getAddress());

		customerRepository.save(c);

		return new ModelAndView("forward:/admin/account");

	}

	// save file
	public void upload(MultipartFile file, String dir) throws IOException {
		Path path = Paths.get(dir);
		InputStream inputStream = file.getInputStream();
		Files.copy(inputStream, path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
	}
}
