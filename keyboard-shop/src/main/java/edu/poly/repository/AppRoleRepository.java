package edu.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.poly.domain.AppRole;

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
	
}
