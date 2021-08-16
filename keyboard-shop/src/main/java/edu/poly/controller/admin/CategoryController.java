package edu.poly.controller.admin;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.Category;
import edu.poly.domain.Product;
import edu.poly.model.CategoryDto;
import edu.poly.repository.CategoryRepository;
import edu.poly.repository.ProductRepository;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	ProductRepository productRepository;

	@GetMapping("/add")
	public String add(ModelMap model) {
		model.addAttribute("category", new CategoryDto());
		
		//set active front-end
		model.addAttribute("menuCa", "menu");
		return "/admin/addCategory";
	}
	
	@PostMapping("/reset")
	public String reset(ModelMap model) {
		model.addAttribute("category", new CategoryDto());
		
		//set active front-end
		model.addAttribute("menuCa", "menu");
		return "/admin/addCategory";
	}

	@PostMapping("/add")
	public ModelAndView addd(ModelMap model, @Valid @ModelAttribute("category") CategoryDto dto, BindingResult result) {
		if (result.hasErrors()) {
			
			//set active front-end
			model.addAttribute("menuCa", "menu");
			return new ModelAndView("admin/addCategory");
		}
		if (!checkCategory(dto.getName()) && !dto.isEdit()) {
			model.addAttribute("error", "Nhãn hiệu này đã tồn tại!");
			
			//set active front-end
			model.addAttribute("menuCa", "menu");
			return new ModelAndView("admin/addCategory", model);
		}

		Category c = new Category();
		BeanUtils.copyProperties(dto, c);
		categoryRepository.save(c);
		if(dto.isEdit()) {
			model.addAttribute("message", "Sửa thành công!");
		} else {
			model.addAttribute("message", "Thêm thành công!");
		}
		
		
		//set active front-end
		model.addAttribute("menuCa", "menu");
		return new ModelAndView("forward:/admin/categories", model);
	}

	@GetMapping("/edit/{id}")
	public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
		Optional<Category> opt = categoryRepository.findById(id);
		CategoryDto dto = new CategoryDto();
		if (opt.isPresent()) {
			Category entity = opt.get();
			BeanUtils.copyProperties(entity, dto);
			dto.setEdit(true);
			model.addAttribute("category", dto);
			
			//set active front-end
			model.addAttribute("menuCa", "menu");
			return new ModelAndView("/admin/addCategory", model);
		}

		model.addAttribute("error", "Không tồn tại thương hiệu này!");

		//set active front-end
		model.addAttribute("menuCa", "menu");
		return new ModelAndView("forward:/admin/addCategories", model);
	}

	@GetMapping("/delete/{id}")
	public ModelAndView delete(@PathVariable("id") Long id, ModelMap model) {
		Optional<Category> opt = categoryRepository.findById(id);
		if (opt.isPresent()) {
//			categoryRepository.deleteProductByCategoryId(id);
			
			Page<Product> listP = productRepository.findAllProductByCategoryId(id, PageRequest.of(0, 100));
			if(listP.getTotalElements() > 0) {
				model.addAttribute("error", "Sản phẩm của nhãn hàng này vẫn còn, xin hãy xoá sản phẩm đó trước!");
			} else {
				categoryRepository.delete(opt.get());
				model.addAttribute("message", "Xoá thành công!");
			}
			
		} else {			
			model.addAttribute("error", "Thương hiệu này không tồn tại!");
		}
		
		//set active front-end
		model.addAttribute("menuCa", "menu");
		return new ModelAndView("forward:/admin/categories", model);
	}

	@GetMapping("/search")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name,
			@RequestParam("size") Optional<Integer> size) {
		int pageSize = size.orElse(5);
		Pageable pageable = PageRequest.of(0, pageSize);
		Page<Category> list = categoryRepository.findByNameContaining(name, pageable);
		model.addAttribute("categories", list);
		model.addAttribute("name", name);
		
		//set active front-end
		model.addAttribute("menuCa", "menu");
		return "admin/category";
	}

	@RequestMapping("/page")
	public String page(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size, @RequestParam(name = "name", required = false) String name) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		if(name.equalsIgnoreCase("null")) {
			name = "";
		}
		Pageable pageable = PageRequest.of(currentPage, pageSize);
		Page<Category> list = categoryRepository.findByNameContaining(name, pageable);
		model.addAttribute("categories", list);
		model.addAttribute("name", name);
		
		//set active front-end
		model.addAttribute("menuCa", "menu");
		return "admin/category";
	}

	@RequestMapping("")
	public String list(ModelMap model, @RequestParam("size") Optional<Integer> size) {
		int pageSize = size.orElse(5);
		Pageable pageable = PageRequest.of(0, pageSize);
		Page<Category> list = categoryRepository.findAll(pageable);
		model.addAttribute("categories", list);
		
		//set active front-end
		model.addAttribute("menuCa", "menu");
		return "/admin/category";
	}

	// Kiem tra ten thuong hieu
	boolean checkCategory(String name) {
		List<Category> list = categoryRepository.findAll();
		for (Category item : list) {
			if (item.getName().equalsIgnoreCase(name)) {
				return false;
			}
		}
		return true;
	}
}
