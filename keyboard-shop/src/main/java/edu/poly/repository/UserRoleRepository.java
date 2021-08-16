package edu.poly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.poly.domain.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>{
	
	@Query(value = "select top 1 * from user_role where customer_id = ?", nativeQuery = true)
	Optional<UserRole> findByCustomerId(Long customerId);
}
