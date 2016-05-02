package Management.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;




@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static PasswordEncoder encoder;

    @Autowired
    private UserDetailsService customUserDetailsService;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
    	http
		.authorizeRequests()
			.antMatchers("/login/**").authenticated()
			.anyRequest().permitAll()
				.and()
			.httpBasic();
			
    	
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(customUserDetailsService)
        .passwordEncoder(passwordEncoder());
    }
    
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }
    
    
    
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        if(encoder == null) {
            encoder = new BCryptPasswordEncoder();
        }

        return encoder;
    }
}