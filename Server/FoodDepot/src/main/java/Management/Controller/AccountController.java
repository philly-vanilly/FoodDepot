package Management.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Management.Model.Account;
import Management.Model.Message;
import Management.Model.Login.Registration;
import Management.Model.Login.User;
import Management.Service.AccountService;

@RestController
public class AccountController {

   
	
	private AccountService accountService;
	
	@Autowired
	public AccountController(AccountService accountService){
		this.accountService = accountService;
	}
	
	
	

    @RequestMapping("/getAccount")
    public Registration greeting(@RequestParam(value="name", defaultValue="World") String name) {
    	
   
    	return new Registration("max", "max@mustermann.de", "123", "Max", "Mustermann");
    }
    
    @RequestMapping(value = "/createAccount", method = RequestMethod.POST)
    public Message createAccount(@Valid Registration registration, BindingResult result) {
    	//Registration registration = new Registration("max", "max@mustermann.de", "123", "Max", "Mustermann");
    	Message message = new Message(false, "The User wasn't created");
    	if (!result.hasErrors()) {
//	    	User user = User.createUser(
//	    			registration.getUsername(),
//	    			registration.getEmail(),
//					registration.getPassword(), 
//					registration.getFirstName(), 
//					registration.getLastName());
	    	
	    	
	    	//accountService.createAccount(user);
	    	message = new Message(true, "The Users was created.");
    	} 
    	return message;
     
    }
    
    @RequestMapping("/login/account")
    public Account test2(@RequestParam(value="name", defaultValue="World") String name) {
        return new Account("Hans", "Franz","Hansi","123");
    }
    @RequestMapping("/login/account2")
    public Account test3(@RequestParam(value="name", defaultValue="World") String name) {
        return new Account("Hans", "Franz","Hansi","123");
    }
    
    @RequestMapping("/changePassword")
    public Message changePassword(@RequestParam(value="email", defaultValue="World") String email,
    								@RequestParam(value="oldPassword", defaultValue="World") String oldPassword,
    									@RequestParam(value="newPassword", defaultValue="World") String newPassword) {
        
    	Boolean result = accountService.changeUserPassword(email, oldPassword, newPassword);
    	
    	if(result){
    		return new Message(true, "The Password was changed.");
    	} else {
    		return new Message(false, "There went something wrong.");
    	}
    	
    }
    
    @RequestMapping("/login")
    public Message login() {	
    	Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
    	if(auth.isAuthenticated()){
    		return new Message(true, "You are authenticated.");
    	} else {
    		return new Message(false, "You are not authenticated.");
    	}
    }
   
    @RequestMapping("/logout")
    public Message logout(HttpServletRequest request, HttpServletResponse response) {
        
    	try {
			request.logout();
			return new Message(true, "You were logged out.");
		} catch (ServletException e) {
			return new Message(true, "You were not logged out.");
		}
        
    }
    
    
    @RequestMapping("/login/getAccount")
    public User getAccount() {
    	String id = this.getUserID();
        return accountService.findByID(id);
    }
    
    private String getUserID() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		return auth.getName();
	}
    
    
    
    

    
    
    
}
