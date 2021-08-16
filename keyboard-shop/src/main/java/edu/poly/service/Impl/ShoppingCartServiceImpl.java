package edu.poly.service.Impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import edu.poly.domain.CartItem;
import edu.poly.service.ShoppingCartService;

@Service
@SessionScope
public class ShoppingCartServiceImpl implements ShoppingCartService{
	private Map<Long, CartItem> map = new HashMap<Long, CartItem>();
	
	@Override
	public void add(CartItem item) {
		CartItem existedItem = map.get(item.getProductId());
		if (existedItem != null) {
			existedItem.setQuantity(item.getQuantity()+existedItem.getQuantity());
		} else {
			map.put(item.getProductId(), item);
		}
	}
	
	@Override
	public void remove(Long id) {
		map.remove(id);
	}
	
	@Override
	public Collection<CartItem> getCartItems() {
		return map.values();
	}
	
	@Override
	public void clear() {
		map.clear();
	}
	
	@Override
	public void update(Long id, int quantity) {
		CartItem item = map.get(id);
		item.setQuantity(quantity);
		if(item.getQuantity()<=0) {
			map.remove(id);
		}
	}
	
	@Override
	public double getAmount() {
		double amount = 0;
		Set<Long> listKey = map.keySet();
		for(Long key : listKey) {
			amount += map.get(key).getPrice() * map.get(key).getQuantity();
		}
		
		return amount;
	}
	
	@Override
	public int getCount() {
		if(map.isEmpty()) {
			return 0;
		}
		return map.values().size();
	}
}
 