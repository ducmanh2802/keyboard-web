package edu.poly.controller.site;

import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.CartItem;
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
public class CartController {
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ShoppingCartService shoppingCartService;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
	
	@RequestMapping("/addCart/{id}")
	public ModelAndView addCart(ModelMap model, @PathVariable("id") Long id, Principal principal) {
		
		boolean isLogin = false;
		if (principal!=null) {
			System.out.println(principal.getName());
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
		
		Optional<Product> p = productRepository.findById(id);
		if (p.isPresent()) {
			CartItem item = new CartItem();
			BeanUtils.copyProperties(p.get(), item);
			item.setDateAdd(new Date());
			item.setPrice(p.get().getUnitPrice() - p.get().getUnitPrice() * p.get().getDiscount() / 100);
			item.setQuantity(1);
			shoppingCartService.add(item);
//			model.addAttribute("message", "Đã thêm vào giỏ hàng!");
		} else {
			model.addAttribute("message", "Sản phẩm này không tồn tại!");
		}
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		return new ModelAndView("forward:/home", model);
	}

	@RequestMapping("/cart/update")
	public ModelAndView updateCart(@RequestParam("id") Long id, @RequestParam("quantity") int quantity, ModelMap model, Principal principal) {
		
		boolean isLogin = false;
		if (principal!=null) {
			System.out.println(principal.getName());
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
		
		shoppingCartService.update(id, quantity);
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		return new ModelAndView("forward:/cart", model);
	}
	
	@RequestMapping("/cart/remove/{id}")
	public ModelAndView remove(@PathVariable("id") Long id, ModelMap model, Principal principal) {
		
		boolean isLogin = false;
		if (principal!=null) {
			System.out.println(principal.getName());
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
		
		shoppingCartService.remove(id);
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		return new ModelAndView("forward:/cart", model);
	}

	@RequestMapping("/cart")
	public ModelAndView cart(ModelMap model, Principal principal) {
		
		boolean isLogin = false;
		if (principal!=null) {
			System.out.println(principal.getName());
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
		
		Collection<CartItem> cart = shoppingCartService.getCartItems();
		model.addAttribute("cartItems", cart);

		double amount = shoppingCartService.getAmount();
		model.addAttribute("amount", amount);

		List<Category> listC = categoryRepository.findAll();
		model.addAttribute("categories", listC);
		model.addAttribute("totalCartItems", shoppingCartService.getCount());
		return new ModelAndView("/site/cart");
	}

}
