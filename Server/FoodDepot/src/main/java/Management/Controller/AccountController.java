package Management.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import Management.Model.AccountClient;
import Management.Model.Message;
import Management.Model.RegistrationImpl;
import Management.Model.Login.User;
import Management.Service.AccountService;

@Controller
public class AccountController {

   
	
	private AccountService accountService;
	
	@Autowired
	public AccountController(AccountService accountService){
		this.accountService = accountService;
	}
	
	

    
    
    @RequestMapping(value = "/createAccount", method = RequestMethod.POST)
    public @ResponseBody Message createAccount(@RequestBody RegistrationImpl registration, BindingResult result) {
    	
    	if(registration != null){
    		System.out.println(registration.getFirstName());		
    		
    	}
    	
    	Message message = new Message(false, "The User wasn't created");
    	if (!result.hasErrors()) {
	    	if(registration != null){
	    		User user = User.createUser(
		    			registration.getUsername(),
		    			registration.getEmail(),
						registration.getPassword(), 
						registration.getFirstName(), 
						registration.getLastName());
	    		accountService.createAccount(user);
	    		message = new Message(true, "The Users was created.");
	    	}
  	
    	} 
    	return message;
     
    }
    
   
    
    @RequestMapping("/changePassword")
    public @ResponseBody Message changePassword(@RequestParam(value="email", defaultValue="World") String email,
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
    public @ResponseBody Message login() {	
    	Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
    	if(auth.isAuthenticated()){
    		return new Message(true, "You are authenticated.");
    	} else {
    		return new Message(false, "You are not authenticated.");
    	}
    }
   
    @RequestMapping("/logout")
    public @ResponseBody Message logout(HttpServletRequest request, HttpServletResponse response) {
        
    	try {
			request.logout();
			return new Message(true, "You were logged out.");
		} catch (ServletException e) {
			return new Message(true, "You were not logged out.");
		}
        
    }
    
    
    @RequestMapping("/getAccount")
    public @ResponseBody AccountClient getAccount() {
    	String id = this.getUserID();
    	AccountClient result = accountService.findByID(id);
    	return result;
    }
    
    private String getUserID() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		return auth.getName();
	}
    
    
    
    

    
    
    
}
