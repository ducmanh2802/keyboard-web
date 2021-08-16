package edu.poly.config.oauth;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import edu.poly.domain.AppRole;
import edu.poly.domain.AuthenticationProvider;
import edu.poly.domain.Customer;
import edu.poly.domain.UserRole;
import edu.poly.repository.AppRoleRepository;
import edu.poly.repository.CustomerRepository;
import edu.poly.repository.UserRoleRepository;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	AppRoleRepository appRoleRepository;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
		String email = oauth2User.getName();
		System.out.println(email);
		Optional<Customer> cus = customerRepository.FindByEmail(email);
		if(cus.isEmpty()) {
			Customer c = new Customer();
			c.setRegisterDate(new Date());
			c.setStatus(true);
			c.setImage("user.png");
			c.setName(oauth2User.getNameReal());
			c.setEmail(email);
			c.setGender(true);
			c.setPassword(bCryptPasswordEncoder.encode("123@ABCxyzalualua"));
			c.setAddress("Chưa có");
			c.setPhone("");
			c.setAuthProvider(AuthenticationProvider.FAECBOOK);
			customerRepository.save(c);
			Optional<AppRole> a = appRoleRepository.findById(2L);
			UserRole urole = new UserRole(0L, c, a.get());
			userRoleRepository.save(urole);
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
}
