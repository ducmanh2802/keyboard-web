package edu.poly.controller.site;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.Category;
import edu.poly.domain.Customer;
import edu.poly.domain.Product;
import edu.poly.domain.UserRole;
import edu.poly.repository.CategoryRepository;
import edu.poly.repository.CustomerRepository;
import edu.poly.repository.ProductRepository;
import edu.poly.repository.UserRoleRepository;
import edu.poly.service.ShoppingCartService;

@Controller
public class HomeController {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ShoppingCartService shoppingCartService;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	UserRoleRepository userRoleRepository;

	@RequestMapping(value = {"/home", "/"})
	public ModelAndView home(ModelMap model, Principal principal) {
		boolean isLogin = false;
		if (principal != null) {
			isLogin = true;
		}
		model.addAttribute("isLogin", isLogin);

		if(principal!=null) {
			Optional<Customer> c = customerRepository.FindByEmail(principal.getName());
			Optional<UserRole> uRole = userRoleRepository.findByCustomerId(Long.valueOf(c.get().getCustomerId()));
			if(uRole.get().getAppRole().getName().equals("ROLE_ADMIN")) {
				return new ModelAndView("forward:/admin/customers", model);
			}
		}

		Page<Product> listP = productRepository.findAll(PageRequest.of(0, 6));

		int totalPage = listP.getTotalPages();
		if (totalPage > 0) {
			int start = 1;
			int end = Math.min(2, totalPage);
			List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		model.addAttribute("products", listP);
		model.addAttribute("slide", true);
		return new ModelAndView("/site/index", model);
	}

	@RequestMapping("/home/item/{id}")
	public ModelAndView item(ModelMap model, @PathVariable("id") Long id, Principal principal) {

		boolean isLogin = false;
		if (principal != null) {
			System.out.println(principal.getName());
			isLogin = true;
		}
		model.addAttribute("isLogin", isLogin);

		if (principal != null) {
			Optional<Customer> c = customerRepository.FindByEmail(principal.getName());
			Optional<UserRole> uRole = userRoleRepository.findByCustomerId(Long.valueOf(c.get().getCustomerId()));
			if (uRole.get().getAppRole().getName().equals("ROLE_ADMIN")) {
				return new ModelAndView("forward:/admin/customers", model);
			}
		}

		Optional<Product> p = productRepository.findById(id);

		// random item product
		Long quantity = productRepository.count();
		int index = (int) (Math.random() * (quantity / 6));
		if (index > quantity - 6) {
			index -= 6;
		}
		Page<Product> productSuggest = productRepository.findAll(PageRequest.of(index, 6));
		if (p.isPresent()) {
			model.addAttribute("product", p.get());
			model.addAttribute("productSuggest", productSuggest);
			List<Category> listC = categoryRepository.findAll();
			model.addAttribute("categories", listC);
			model.addAttribute("totalCartItems", shoppingCartService.getCount());
			return new ModelAndView("/site/item", model);
		} else {
			model.addAttribute("message", "Sản phẩm đã bị xoá !");
		}
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		return new ModelAndView("forward:/home", model);
	}

	@RequestMapping("/home/brand/{id}")
	public ModelAndView brand(ModelMap model, @PathVariable("id") Long categoryId, Principal principal) {
		boolean isLogin = false;
		if (principal != null) {
			System.out.println(principal.getName());
			isLogin = true;
		}
		model.addAttribute("isLogin", isLogin);

		if (principal != null) {
			Optional<Customer> c = customerRepository.FindByEmail(principal.getName());
			Optional<UserRole> uRole = userRoleRepository.findByCustomerId(Long.valueOf(c.get().getCustomerId()));
			if (uRole.get().getAppRole().getName().equals("ROLE_ADMIN")) {
				return new ModelAndView("forward:/admin/customers", model);
			}
		}

		Page<Product> listP = productRepository.findAllProductByCategoryId(categoryId, PageRequest.of(0, 6));

		System.out.println(listP.getTotalElements());
		int totalPage = listP.getTotalPages();
		if (totalPage > 0) {
			int start = 1;
			int end = Math.min(2, totalPage);
			List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		model.addAttribute("brand", categoryId);
		model.addAttribute("products", listP);
		model.addAttribute("slide", true);
		return new ModelAndView("/site/index", model);
	}

	@RequestMapping("/home/page")
	public ModelAndView page(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("name") String name, @RequestParam("filter") Optional<Integer> filter,
			@RequestParam("brand") Long brand, Principal principal) {

		boolean isLogin = false;
		if (principal != null) {
			System.out.println(principal.getName());
			isLogin = true;
		}
		model.addAttribute("isLogin", isLogin);

		if (principal != null) {
			Optional<Customer> c = customerRepository.FindByEmail(principal.getName());
			Optional<UserRole> uRole = userRoleRepository.findByCustomerId(Long.valueOf(c.get().getCustomerId()));
			if (uRole.get().getAppRole().getName().equals("ROLE_ADMIN")) {
				return new ModelAndView("forward:/admin/customers", model);
			}
		}

		int currentPage = page.orElse(0);
		int filterPage = filter.orElse(0);

		Pageable pageable = PageRequest.of(currentPage, 6);

		if (brand != -1) {
			if (filterPage == 0) {
				pageable = PageRequest.of(currentPage, 6);
			} else if (filterPage == 1) {
				pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.DESC, "entered_date"));
			} else if (filterPage == 2) {
				pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.ASC, "entered_date"));
			} else if (filterPage == 3) {
				pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.DESC, "unit_price"));
			} else if (filterPage == 4) {
				pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.ASC, "unit_price"));
			}
		} else {
			if (filterPage == 0) {
				pageable = PageRequest.of(currentPage, 6);
			} else if (filterPage == 1) {
				pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.DESC, "enteredDate"));
			} else if (filterPage == 2) {
				pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.ASC, "enteredDate"));
			} else if (filterPage == 3) {
				pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.DESC, "unitPrice"));
			} else if (filterPage == 4) {
				pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.ASC, "unitPrice"));
			}
		}

		Page<Product> listP = null;
		if (brand == -1) {
			listP = productRepository.findByNameContaining(name, pageable);
		} else {
			listP = productRepository.findAllProductByCategoryId(brand, pageable);
		}

		int totalPage = listP.getTotalPages();
		if (totalPage > 0) {
			int start = Math.max(1, currentPage - 2);
			int end = Math.min(currentPage + 2, totalPage);
			if (totalPage > 5) {
				if (end == totalPage) {
					start = end - 5;
				} else if (start == 1) {
					end = start + 5;
				}
			}
			List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		model.addAttribute("brand", brand);
		model.addAttribute("filter", filterPage);
		model.addAttribute("name", name);
		model.addAttribute("products", listP);
		model.addAttribute("slide", true);
		return new ModelAndView("/site/index", model);
	}

	@RequestMapping("/home/search")
	public ModelAndView search(ModelMap model, @RequestParam("name") String name,
			@RequestParam("filter") Optional<Integer> filter, Principal principal) {

		boolean isLogin = false;
		if (principal != null) {
			System.out.println(principal.getName());
			isLogin = true;
		}
		model.addAttribute("isLogin", isLogin);

		if (principal != null) {
			Optional<Customer> c = customerRepository.FindByEmail(principal.getName());
			Optional<UserRole> uRole = userRoleRepository.findByCustomerId(Long.valueOf(c.get().getCustomerId()));
			if (uRole.get().getAppRole().getName().equals("ROLE_ADMIN")) {
				return new ModelAndView("forward:/admin/customers", model);
			}
		}

		int filterPage = filter.orElse(0);
		Pageable pageable = PageRequest.of(0, 6);

		if (filterPage == 0) {
			pageable = PageRequest.of(0, 6);
		} else if (filterPage == 1) {
			pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "enteredDate"));
		} else if (filterPage == 2) {
			pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "enteredDate"));
		} else if (filterPage == 3) {
			pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "unitPrice"));
		} else if (filterPage == 4) {
			pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "unitPrice"));
		}
		Page<Product> listP = productRepository.findByNameContaining(name, pageable);

		int totalPage = listP.getTotalPages();
		if (totalPage > 0) {
			int start = 1;
			int end = Math.min(2, totalPage);
			List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		model.addAttribute("totalCartItems", shoppingCartService.getCount());

		model.addAttribute("name", name);
		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		model.addAttribute("filter", filterPage);
		model.addAttribute("products", listP);
		model.addAttribute("slide", true);
		return new ModelAndView("/site/index", model);
	}

}
