package edu.poly.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.poly.domain.Product;
import edu.poly.model.ReportInventory;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Page<Product> findByNameContaining(String name, Pageable page);

//	Page<Product> findAllByCategory(Pageable page);

	@Query(value = "select * from products where category_id = ?", nativeQuery = true)
	Page<Product> findAllProductByCategoryId(Long id, Pageable pageable);

	@Query("select new edu.poly.model.ReportInventory(o.category, sum(o.unitPrice), sum(o.quantity)) from Product o group by o.category order by sum(o.quantity) desc")
	List<ReportInventory> getInventoryByCategory();

	@Query(value = "select categories.category_name , sum(orderdetails.unit_price) as 'Tổng tiền', COUNT(products.quantity) as 'Số lượng' \r\n"
			+ "from orderdetails \r\n" + "join orders on orders.order_id = orderdetails.order_id\r\n"
			+ "join products on orderdetails.product_id = products.product_id \r\n"
			+ "join categories on categories.category_id = products.category_id\r\n" + "where orders.status = 2\r\n"
			+ "group by categories.category_name\r\n" + "order by COUNT(products.quantity) desc", nativeQuery = true)
	List<Object[]> getBestSellingCategory();

	@Query(value = "select products.name , sum(orderdetails.unit_price) as 'Tổng tiền', COUNT(products.quantity) as 'Số lượng' from orderdetails \r\n"
			+ "join orders on orders.order_id = orderdetails.order_id\r\n"
			+ "join products on orderdetails.product_id = products.product_id \r\n" + "where orders.status = 2\r\n"
			+ "group by products.name\r\n" + "order by COUNT(products.quantity) desc", nativeQuery = true)
	List<Object[]> getBestSellingProduct();

	@Query(value = "select orders.customer_id, customers.name as 'Ten', COUNT(orders.order_id) as 'Tong so don', sum(orders.amount) as 'Tong tien'\r\n"
			+ "from orders\r\n"
			+ "join customers on customers.customer_id = orders.customer_id\r\n"
			+ "where orders.status = 2\r\n"
			+ "group by orders.customer_id,customers.name \r\n"
			+ "order by COUNT(orders.order_id) desc", nativeQuery = true)
	List<Object[]> getBestBuyer();

	@Query(value = "select order_date as 'Time' ,count(order_id) as 'so luong',sum(amount) as 'tong tien' from orders where status = 2\r\n"
			+ "group by order_date\r\n"
			+ "order by order_date desc", nativeQuery = true)
	List<Object[]> getStatisticalByDay();

	@Query(value = "select   cast(year(order_date) as varchar) + '-' +cast(month(order_date) as varchar) month, \r\n"
			+ "count(order_id) as 'count', sum(amount) as 'sum' from orders where status = 2\r\n"
			+ "group by month(order_date), year(order_date)\r\n"
			+ "order by month desc", nativeQuery = true)
	List<Object[]> getStatisticalByMonth();

	@Query(value = "select year(order_date) as 'year',count(order_id) as 'count', sum(amount) as 'sum' from orders where status = 2\r\n"
			+ "group by year(order_date)\r\n"
			+ "order by year(order_date) desc", nativeQuery = true)
	List<Object[]> getStatisticalByYear();
}