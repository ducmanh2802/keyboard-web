package edu.poly.controller.admin;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.Order;
import edu.poly.domain.OrderDetail;
import edu.poly.domain.Product;
import edu.poly.repository.OrderDetailRepository;
import edu.poly.repository.OrderRepository;
import edu.poly.repository.ProductRepository;
import edu.poly.service.SendMailService;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OrderDetailRepository orderDetailRepository;
	
	@Autowired
	SendMailService sendMailService;
	
	@Autowired
	ProductRepository productRepository;

	@RequestMapping("")
	public ModelAndView order(ModelMap model) {

		Page<Order> listO = orderRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderId")));

		model.addAttribute("orders", listO);
		// set active front-end
		model.addAttribute("menuO", "menu");
		return new ModelAndView("/admin/order");
	}

	@RequestMapping("/page")
	public ModelAndView page(ModelMap model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size, @RequestParam("filter") Optional<Integer> filter) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		int filterPage = filter.orElse(0);

		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "orderId"));
		Page<Order> listO = null;
		if (filterPage == 0) {
			listO = orderRepository.findAll(pageable);
		} else if (filterPage == 1) {
			pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "order_id"));
			listO = orderRepository.findByStatus(0, pageable);
		} else if (filterPage == 2) {
			pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "order_id"));
			listO = orderRepository.findByStatus(1, pageable);
		} else if (filterPage == 3) {
			pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "order_id"));
			listO = orderRepository.findByStatus(2, pageable);
		} else if (filterPage == 4) {
			pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "order_id"));
			listO = orderRepository.findByStatus(3, pageable);
		} else if (filterPage == 5) {
			pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "amount"));
			listO = orderRepository.findAll(pageable);
		}

		model.addAttribute("filter", filterPage);
		model.addAttribute("page", currentPage);
		model.addAttribute("orders", listO);
		// set active front-end
		model.addAttribute("menuO", "menu");
		return new ModelAndView("/admin/order");
	}

	@RequestMapping("/search")
	public ModelAndView search(ModelMap model, @RequestParam("id") String id) {
		Page<Order> listO = null;
		if (id == null || id.equals("") || id.equalsIgnoreCase("null")) {
			listO = orderRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderId")));
		} else {
			listO = orderRepository.findByorderId(Integer.valueOf(id), PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderId")));
		}

		model.addAttribute("id", id);
		model.addAttribute("orders", listO);
		// set active front-end
		model.addAttribute("menuO", "menu");
		return new ModelAndView("/admin/order");
	}

	@RequestMapping("/cancel/{order-id}")
	public ModelAndView cancel(ModelMap model, @PathVariable("order-id") int id) {
		Optional<Order> o = orderRepository.findById(id);
		if (o.isEmpty()) {
			return new ModelAndView("forward:/admin/orders", model);
		}
		Order oReal = o.get();
		oReal.setStatus((short) 3);
		orderRepository.save(oReal);
		
		sendMailAction(oReal, "Bạn đã bị huỷ 1 đơn hàng từ KeyBoard Shop!",
				"Chúng tôi rất tiếc!", "Thông báo huỷ đơn hàng!");
		
		return new ModelAndView("forward:/admin/orders", model);
	}

	@RequestMapping("/confirm/{order-id}")
	public ModelAndView confirm(ModelMap model, @PathVariable("order-id") int id) {
		Optional<Order> o = orderRepository.findById(id);
		if (o.isEmpty()) {
			return new ModelAndView("forward:/admin/orders", model);
		}
		Order oReal = o.get();
		oReal.setStatus((short) 1);
		orderRepository.save(oReal);
		
		sendMailAction(oReal, "Bạn có 1 đơn hàng ở KeyBoard Shop đã được xác nhận!",
				"Chúng tôi sẽ sớm giao hàng cho bạn!", "Thông báo đơn hàng đã được xác nhận!");
		
		return new ModelAndView("forward:/admin/orders", model);
	}

	@RequestMapping("/delivered/{order-id}")
	public ModelAndView delivered(ModelMap model, @PathVariable("order-id") int id) {
		Optional<Order> o = orderRepository.findById(id);
		if (o.isEmpty()) {
			return new ModelAndView("forward:/admin/orders", model);
		}
		Order oReal = o.get();
		oReal.setStatus((short) 2);
		orderRepository.save(oReal);
		
		Product p = null;
		List<OrderDetail> listDe = orderDetailRepository.findByOrderId(id);
		for(OrderDetail od : listDe) {
			p = od.getProduct();
			p.setQuantity(p.getQuantity()-od.getQuantity());
			productRepository.save(p);
		}
		
		sendMailAction(oReal, "Bạn có 1 đơn hàng ở KeyBoard Shop đã thanh toán thành công!",
				"Chúng tôi cám ơn bạn vì đã ủng hộ KeyBoard Shop!", "Thông báo thanh toán thành công!");
		
		return new ModelAndView("forward:/admin/orders", model);
	}

	@RequestMapping("/detail/{order-id}")
	public ModelAndView detail(ModelMap model, @PathVariable("order-id") int id) {

		List<OrderDetail> listO = orderDetailRepository.findByOrderId(id);

		model.addAttribute("amount", orderRepository.findById(id).get().getAmount());
		model.addAttribute("orderDetail", listO);
		model.addAttribute("orderId", id);
		// set active front-end
		model.addAttribute("menuO", "menu");
		return new ModelAndView("/admin/detail", model);
	}
	

	// format currency
	public String format(String number) {
		DecimalFormat formatter = new DecimalFormat("###,###,###.##");

		return formatter.format(Double.valueOf(number)) + " VNĐ";
	}

	// sendmail
		public void sendMailAction(Order oReal, String status, String cmt, String notifycation) {
			List<OrderDetail> list = orderDetailRepository.findByOrderId(oReal.getOrderId());
			System.out.println(oReal.getOrderId());

			StringBuilder stringBuilder = new StringBuilder();
			int index = 0;
			stringBuilder.append("<h3>Xin chào " + oReal.getCustomer().getName() + "!</h3>\r\n" + "    <h4>" + status + "</h4>\r\n"
					+ "    <table style=\"border: 1px solid gray;\">\r\n"
					+ "        <tr style=\"width: 100%; border: 1px solid gray;\">\r\n"
					+ "            <th style=\"border: 1px solid gray;\">STT</th>\r\n"
					+ "            <th style=\"border: 1px solid gray;\">Tên sản phẩm</th>\r\n"
					+ "            <th style=\"border: 1px solid gray;\">Số lượng</th>\r\n"
					+ "            <th style=\"border: 1px solid gray;\">Đơn giá</th>\r\n" + "        </tr>");
			for (OrderDetail oD : list) {
				index++;
				stringBuilder.append("<tr>\r\n" + "            <td style=\"border: 1px solid gray;\">" + index + "</td>\r\n"
						+ "            <td style=\"border: 1px solid gray;\">" + oD.getProduct().getName() + "</td>\r\n"
						+ "            <td style=\"border: 1px solid gray;\">" + oD.getQuantity() + "</td>\r\n"
						+ "            <td style=\"border: 1px solid gray;\">" + format(String.valueOf(oD.getUnitPrice()))
						+ "</td>\r\n" + "        </tr>");
			}
			stringBuilder.append("\r\n" + "    </table>\r\n" + "    <h3>Tổng tiền: "
					+ format(String.valueOf(oReal.getAmount())) + "</h3>\r\n" + "    <hr>\r\n" + "    <h5>" + cmt
					+ "</h5>\r\n" + "    <h5>Chúc bạn 1 ngày tốt lành!</h5>");

			sendMailService.queue(oReal.getCustomer().getEmail().trim(), notifycation, stringBuilder.toString());
		}
}
