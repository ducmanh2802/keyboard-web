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

import edu.poly.domain.Category;
import edu.poly.domain.OrderDetail;
import edu.poly.domain.Product;
import edu.poly.model.ProductDto;
import edu.poly.repository.CategoryRepository;
import edu.poly.repository.OrderDetailRepository;
import edu.poly.repository.ProductRepository;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@RequestMapping("")
	public ModelAndView list(ModelMap model) {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Product> list = productRepository.findAll(pageable);

		model.addAttribute("products", list);
		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		// set active front-end
		model.addAttribute("menuP", "menu");
		return new ModelAndView("/admin/product", model);
	}

	@GetMapping("/add")
	public ModelAndView add(ModelMap model) {
		model.addAttribute("product", new ProductDto());
		model.addAttribute("photo", "keyboard.png");
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);

		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		// set active front-end
		model.addAttribute("menuP", "menu");
		return new ModelAndView("/admin/addProduct", model);
	}

	@PostMapping("/reset")
	public ModelAndView reset(ModelMap model) {
		model.addAttribute("product", new ProductDto());
		model.addAttribute("photo", "keyboard.png");
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);

		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		// set active front-end
		model.addAttribute("menuP", "menu");
		return new ModelAndView("/admin/addProduct", model);
	}

	@PostMapping("/add")
	public ModelAndView addd(ModelMap model, @Valid @ModelAttribute("product") ProductDto dto, BindingResult result,
			@RequestParam("photo") MultipartFile photo, @RequestParam("imgP") String img) throws IOException {
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		if (result.hasErrors()) {
			if (dto.isEdit()) {
				model.addAttribute("photo", img);
				dto.setImage(img);
			} else {
				model.addAttribute("photo", "keyboard.png");
			}
			List<Category> listC = categoryRepository.findAll();
			model.addAttribute("categories", listC);
			// set active front-end
			model.addAttribute("menuP", "menu");
			return new ModelAndView("/admin/addProduct", model);
		}
		Product p = new Product();
		BeanUtils.copyProperties(dto, p);
		p.setCategory(new Category(dto.getCategoryId(),
				categoryRepository.findById(dto.getCategoryId()).get().getName(), true, null));
		p.setEnteredDate(new Date());

		if (photo.getOriginalFilename().equals("")) {
			if (!img.equals("")) {
				p.setImage(img);
			} else {
				p.setImage("keyboard.png");
			}
		} else {
			p.setImage(photo.getOriginalFilename());
			upload(photo, "uploads/products/", p.getImage());
		}

		productRepository.save(p);
		if (dto.isEdit()) {
			model.addAttribute("message", "Sửa thành công!");
		} else {
			model.addAttribute("message", "Thêm thành công!");
		}

		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		// set active front-end
		model.addAttribute("menuP", "menu");
		return new ModelAndView("forward:/admin/products", model);
	}

	@GetMapping("/delete/{id}")
	public ModelAndView delete(@PathVariable("id") Long id, ModelMap model) {
		Optional<Product> p = productRepository.findById(id);
		if (p.isPresent()) {
			List<OrderDetail> listOD = orderDetailRepository.findByProductId(id);
			if (listOD.size() > 0) {
				model.addAttribute("error", "Không thể xoá sản phẩm này!");
			} else {
				productRepository.deleteById(id);
				model.addAttribute("message", "Xoá thành công!");
			}
		} else {
			model.addAttribute("error", "Sản phẩm không tồn tại!");
		}

		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		// set active front-end
		model.addAttribute("menuP", "menu");
		return new ModelAndView("forward:/admin/products", model);
	}

	@GetMapping("/edit/{id}")
	public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
		Optional<Product> p = productRepository.findById(id);
		ProductDto dto = new ProductDto();
		if (p.isPresent()) {
			List<Category> categories = categoryRepository.findAll();
			model.addAttribute("categories", categories);

			BeanUtils.copyProperties(p.get(), dto);
			dto.setEdit(true);
			dto.setCategoryId(p.get().getCategory().getCategoryId());
			model.addAttribute("product", dto);

			model.addAttribute("photo", dto.getImage());

			List<Category> listC = categoryRepository.findAll();
			model.addAttribute("categories", listC);
			// set active front-end
			model.addAttribute("menuP", "menu");
			return new ModelAndView("/admin/addProduct", model);
		}

		model.addAttribute("error", "Sản phẩm này không tồn tại!");
		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		// set active front-end
		model.addAttribute("menuP", "menu");
		return new ModelAndView("forward:/admin/products", model);
	}

	@RequestMapping("/search")
	public ModelAndView search(ModelMap model, @RequestParam("name") String name,
			@RequestParam("size") Optional<Integer> size, @RequestParam("filter") Optional<Integer> filter) {
		int pageSize = size.orElse(5);
		int filterPage = filter.orElse(0);
		Pageable pageable = PageRequest.of(0, pageSize);

		if (filterPage == 0) {
			pageable = PageRequest.of(0, pageSize);
		} else if (filterPage == 1) {
			pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "enteredDate"));
		} else if (filterPage == 2) {
			pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "enteredDate"));
		} else if (filterPage == 3) {
			pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "unitPrice"));
		} else if (filterPage == 4) {
			pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "unitPrice"));
		}

		Page<Product> list = productRepository.findByNameContaining(name, pageable);

		model.addAttribute("name", name);
		model.addAttribute("filter", filterPage);
		model.addAttribute("products", list);
		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		// set active front-end
		model.addAttribute("menuP", "menu");
		return new ModelAndView("/admin/product", model);
	}

	@RequestMapping("/page")
	public ModelAndView page(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam(value = "name", required = false) String name, @RequestParam("size") Optional<Integer> size,
			@RequestParam("filter") Optional<Integer> filter, @RequestParam("brand") Optional<Long> brandPage) {

		int filterPage = filter.orElse(0);
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		if (name.equalsIgnoreCase("null")) {
			name = "";
		}
		Long brand = brandPage.orElse(0L);

		Pageable pageable = PageRequest.of(currentPage, pageSize);

		if (brand == 0) {
			if (filterPage == 0) {
				pageable = PageRequest.of(currentPage, pageSize);
			} else if (filterPage == 1) {
				pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "enteredDate"));
			} else if (filterPage == 2) {
				pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "enteredDate"));
			} else if (filterPage == 3) {
				pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "unitPrice"));
			} else if (filterPage == 4) {
				pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "unitPrice"));
			}
		} else {
			if (filterPage == 0) {
				pageable = PageRequest.of(currentPage, pageSize);
			} else if (filterPage == 1) {
				pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "entered_date"));
			} else if (filterPage == 2) {
				pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "entered_date"));
			} else if (filterPage == 3) {
				pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "unit_price"));
			} else if (filterPage == 4) {
				pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "unit_price"));
			}
		}

		Page<Product> list = null;

		if (brand == 0) {
			list = productRepository.findByNameContaining(name, pageable);
		} else {
			list = productRepository.findAllProductByCategoryId(brand, pageable);
		}

		model.addAttribute("brand", brand);
		model.addAttribute("products", list);
		model.addAttribute("name", name);
		model.addAttribute("filter", filterPage);
		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		// set active front-end
		model.addAttribute("menuP", "menu");
		return new ModelAndView("/admin/product", model);
	}

	// save file
	public void upload(MultipartFile file, String dir, String name) throws IOException {
		Path path = Paths.get(dir);
		InputStream inputStream = file.getInputStream();
		Files.copy(inputStream, path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
	}

	// kiem tra ten san pham
	public Boolean check(String name) {
		List<Product> list = productRepository.findAll();
		for (Product item : list) {
			if (item.getName().equalsIgnoreCase(name))
				return false;
		}

		return true;
	}

}
