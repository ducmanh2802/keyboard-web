package edu.poly.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.repository.ProductRepository;

@Controller
@RequestMapping("/admin")
public class ReportController {
	@Autowired
	ProductRepository productRepository;

	@RequestMapping("/inventory")
	public ModelAndView inventory(ModelMap model) {
		model.addAttribute("inventory", productRepository.getInventoryByCategory());
		// set active front-end
		model.addAttribute("menuI", "menu");
		return new ModelAndView("/admin/inventory", model);
	}

	@RequestMapping("/reports")
	public ModelAndView reports(ModelMap model, @RequestParam("reports") Optional<Integer> reports) {
		int report = reports.orElse(0);
		if(report==0) {
			model.addAttribute("report", report);
			return new ModelAndView("forward:/admin/reports/statistical", model);
		} else if (report == 1) {
			model.addAttribute("report", report);
			return new ModelAndView("forward:/admin/reports/best-selling-category", model);
		} else if (report == 2) {
			model.addAttribute("report", report);
			return new ModelAndView("forward:/admin/reports/best-selling-product", model);
		} else if (report == 3) {
			model.addAttribute("report", report);
			return new ModelAndView("forward:/admin/reports/best-buyer", model);
		}

		model.addAttribute("report", report);
		return new ModelAndView("forward:/admin/reports/best-selling-category", model);
	}

	@RequestMapping("/reports/best-selling-category")
	public ModelAndView bestSellCategory(ModelMap model) {
		List<Object[]> listBestSellingCategory = productRepository.getBestSellingCategory();

		model.addAttribute("listBestSellingCategory", listBestSellingCategory);
		// set active front-end
		model.addAttribute("menuR", "menu");
		return new ModelAndView("/admin/best-selling-category");
	}

	@RequestMapping("/reports/best-selling-product")
	public ModelAndView bestSellProduct(ModelMap model) {
		List<Object[]> listBestSellingProduct = productRepository.getBestSellingProduct();

		model.addAttribute("listBestSellingProduct", listBestSellingProduct);
		// set active front-end
		model.addAttribute("menuR", "menu");
		return new ModelAndView("/admin/best-selling-product");
	}

	@RequestMapping("/reports/best-buyer")
	public ModelAndView bestBuyer(ModelMap model) {
		List<Object[]> listBestBuyer = productRepository.getBestBuyer();

		model.addAttribute("listBestBuyer", listBestBuyer);
		// set active front-end
		model.addAttribute("menuR", "menu");
		return new ModelAndView("/admin/best-buyer");
	}
	
	@RequestMapping("/reports/statistical")
	public ModelAndView statistical(ModelMap model, @RequestParam("statisticalId") Optional<Integer> statisticalId) {
		int idStatistical = statisticalId.orElse(0);
		if(idStatistical==0) {
			model.addAttribute("statisticalId", idStatistical);
			return new ModelAndView("forward:/admin/reports/statistical/day", model);
		} else if(idStatistical==1) {
			model.addAttribute("statisticalId", idStatistical);
			return new ModelAndView("forward:/admin/reports/statistical/month", model);			
		} else if(idStatistical==2) {
			model.addAttribute("statisticalId", idStatistical);
			return new ModelAndView("forward:/admin/reports/statistical/year", model);			
		}
		model.addAttribute("statisticalId", idStatistical);
		// set active front-end
		model.addAttribute("menuR", "menu");
		return new ModelAndView("/admin/statistical-day");
	}
	
	@RequestMapping("/reports/statistical/day")
	public ModelAndView statisticalByDay(ModelMap model) {
		List<Object[]> statistical = productRepository.getStatisticalByDay();
		
		model.addAttribute("statistical", statistical);
		// set active front-end
		model.addAttribute("menuR", "menu");
		return new ModelAndView("/admin/statistical-day");
	}
	
	@RequestMapping("/reports/statistical/month")
	public ModelAndView statisticalByMonth(ModelMap model) {
		List<Object[]> statistical = productRepository.getStatisticalByMonth();
		
		model.addAttribute("statistical", statistical);
		// set active front-end
		model.addAttribute("menuR", "menu");
		return new ModelAndView("/admin/statistical-month");
	}
	
	@RequestMapping("/reports/statistical/year")
	public ModelAndView statisticalByYear(ModelMap model) {
		List<Object[]> statistical = productRepository.getStatisticalByYear();
		
		model.addAttribute("statistical", statistical);
		// set active front-end
		model.addAttribute("menuR", "menu");
		return new ModelAndView("/admin/statistical-year");
	}

}
