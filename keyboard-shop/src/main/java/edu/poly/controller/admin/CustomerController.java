package edu.poly.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.AppRole;
import edu.poly.domain.Customer;
import edu.poly.domain.UserRole;
import edu.poly.model.CustomerDto;
import edu.poly.repository.AppRoleRepository;
import edu.poly.repository.CustomerRepository;
import edu.poly.repository.UserRoleRepository;

@Controller
@RequestMapping("/admin/customers")
public class CustomerController {
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Autowired
	AppRoleRepository appRoleRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@RequestMapping("")
	public ModelAndView form(ModelMap model) {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Customer> list = customerRepository.findAll(pageable);

		model.addAttribute("customers", list);
		// set active front-end
		model.addAttribute("menuC", "menu");
		return new ModelAndView("/admin/manageCustomer", model);
	}

	@GetMapping("/add")
	public ModelAndView add(ModelMap model) {
		model.addAttribute("customer", new CustomerDto());
		model.addAttribute("photo", "user.png");

		// set active front-end
		model.addAttribute("menuC", "menu");
		return new ModelAndView("/admin/addCustomer", model);
	}

	@PostMapping("/add")
	public ModelAndView addd(ModelMap model, @Valid @ModelAttribute("customer") CustomerDto dto, BindingResult result,
			@RequestParam("imgC") String image, @RequestParam("photo") MultipartFile photo,
			@RequestParam("password") String password) throws IOException {
		dto.setPassword(bCryptPasswordEncoder.encode(password));
		if(dto.isEdit()) {
			if(password.equals(customerRepository.findById(dto.getCustomerId()).get().getPassword())) {
				System.out.println("trung   ajaja");
				dto.setPassword(password);
			}
		}
		if (result.hasErrors()) {
			if (dto.isEdit()) {
				model.addAttribute("photo", image);
				dto.setImage(image);
			} else {
				model.addAttribute("photo", "user.png");
			}
			// set active front-end
			model.addAttribute("menuC", "menu");
			return new ModelAndView("/admin/addCustomer", model);
		}

		if (!checkEmail(dto.getEmail()) && !dto.isEdit()) {
			model.addAttribute("photo", "user.png");

			model.addAttribute("error", "Email này đã được sử dụng!");
			// set active front-end
			model.addAttribute("menuC", "menu");
			return new ModelAndView("/admin/addCustomer", model);
		}

		Customer c = new Customer();
		BeanUtils.copyProperties(dto, c);
		c.setRegisterDate(new Date());
		c.setStatus(true);

		if (photo.getOriginalFilename().equals("")) {
			if (image.equals("")) {
				c.setImage("user.png");
			} else {
				c.setImage(image);
			}
		} else {
			c.setImage(photo.getOriginalFilename());
			upload(photo, "uploads/customers");
		}

		customerRepository.save(c);
		Optional<AppRole> a = appRoleRepository.findById(2L);
		UserRole urole = new UserRole(0L, c, a.get());
		userRoleRepository.save(urole);
		if (dto.isEdit()) {
			model.addAttribute("message", "Sửa thành công !");

		} else {
			model.addAttribute("message", "Thêm thành công !");
		}

		// set active front-end
		model.addAttribute("menuC", "menu");
		return new ModelAndView("forward:/admin/customers", model);
	}

	@RequestMapping("/edit/{id}")
	public ModelAndView edit(ModelMap model, @PathVariable("id") int id) {

		Optional<Customer> opt = customerRepository.findById(id);
		if (opt.isPresent()) {
			CustomerDto dto = new CustomerDto();
			BeanUtils.copyProperties(opt.get(), dto);
			dto.setEdit(true);
			model.addAttribute("customer", dto);
			model.addAttribute("photo", dto.getImage());
			// set active front-end
			model.addAttribute("menuC", "menu");
			return new ModelAndView("/admin/addCustomer", model);
		}

		model.addAttribute("error", "Người dùng này không tồn tại!");
		// set active front-end
		model.addAttribute("menuC", "menu");
		return new ModelAndView("forward:/admin/customers", model);
	}

	@RequestMapping("/delete/{id}")
	public ModelAndView delete(ModelMap model, @PathVariable("id") int id) {

		Optional<Customer> opt = customerRepository.findById(id);
		if (opt.isPresent()) {
//			customerRepository.deleteById(id);
//			customerRepository.SetStatus(id);
			Customer c = opt.get();
			c.setStatus(false);
			customerRepository.save(c);
			model.addAttribute("message", "Xoá thành công!");
		} else {
			model.addAttribute("error", "Người dùng này không tồn tại!");
		}

		// set active front-end
		model.addAttribute("menuC", "menu");
		return new ModelAndView("forward:/admin/customers", model);
	}

	@RequestMapping("/page")
	public ModelAndView page(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("name") String name, @RequestParam("size") Optional<Integer> size,
			@RequestParam("filter") Optional<Integer> filter) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		int filterPage = filter.orElse(0);
		if (name.equalsIgnoreCase("null")) {
			name = "";
		}
		Pageable pageable = PageRequest.of(currentPage, pageSize);
		
		if (filterPage == 0) {
			pageable = PageRequest.of(currentPage, pageSize);
		} else if (filterPage == 1) {
			pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "name"));
		} else if (filterPage == 2) {
			pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "name"));
		} else if (filterPage == 3) {
			pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "registerDate"));
		} else if (filterPage == 4) {
			pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "registerDate"));
		}
		
		Page<Customer> list = customerRepository.findByNameContaining(name, pageable);

		model.addAttribute("customers", list);
		model.addAttribute("name", name);
		model.addAttribute("filter", filterPage);
		// set active front-end
		model.addAttribute("menuC", "menu");
		return new ModelAndView("/admin/manageCustomer", model);
	}

	@RequestMapping("/search")
	public ModelAndView search(ModelMap model, @RequestParam("name") String name,
			@RequestParam("size") Optional<Integer> size, @RequestParam("filter") Optional<Integer> filter) {
		int filterPage = filter.orElse(0);
		int pageSize = size.orElse(5);
		Pageable pageable = PageRequest.of(0, pageSize);
		
		if (filterPage == 0) {
			pageable = PageRequest.of(0, pageSize);
		} else if (filterPage == 1) {
			pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "name"));
		} else if (filterPage == 2) {
			pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "name"));
		} else if (filterPage == 3) {
			pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "registerDate"));
		} else if (filterPage == 4) {
			pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "registerDate"));
		}

		Page<Customer> list = customerRepository.findByNameContaining(name, pageable);

		model.addAttribute("name", name);
		model.addAttribute("filter", filterPage);
		model.addAttribute("customers", list);
		// set active front-end
		model.addAttribute("menuC", "menu");
		return new ModelAndView("/admin/manageCustomer", model);
	}

	// save file
	public void upload(MultipartFile file, String dir) throws IOException {
		Path path = Paths.get(dir);
		InputStream inputStream = file.getInputStream();
		Files.copy(inputStream, path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
	}

	// check email
	public boolean checkEmail(String email) {
		List<Customer> list = customerRepository.findAll();
		for (Customer c : list) {
			if (c.getEmail().equalsIgnoreCase(email)) {
				return false;
			}
		}
		return true;
	}
}
