package edu.poly.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.poly.config.oauth.CustomOAuth2UserService;
import edu.poly.config.oauth.OAuth2LoginSuccessHandler;
import edu.poly.service.UserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	UserDetailService userDetailService;
	
	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;
	
	@Autowired
	OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
	

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		http.authorizeRequests().antMatchers("/login", "/oauth2/**", "/logout", "/register", "/home/**", "/cart/**",
				"/addCart/**", "/assets/**", "/css/**", "/js/**").permitAll();

		http.authorizeRequests().antMatchers("/customer/**").access("hasRole('ROLE_USER')");

		http.authorizeRequests().antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')");

		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

		http.authorizeRequests().and().formLogin()
				.loginProcessingUrl("/j_spring_security_check")
				.loginPage("/login")
				.defaultSuccessUrl("/home")
				.failureUrl("/login?error=true")
				.usernameParameter("username")
				.passwordParameter("password")
				.and()
				.oauth2Login()
					.loginPage("/login")
					.userInfoEndpoint().userService(customOAuth2UserService).and()
					.successHandler(oauth2LoginSuccessHandler)
				.and()
				.logout().logoutUrl("/logout").logoutSuccessUrl("/home");

	}



}
