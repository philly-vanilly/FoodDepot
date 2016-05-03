package Management.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
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
    
    @RequestMapping("/createAccount")
    public Message createAccount() {
    	Registration registration = new Registration("max", "max@mustermann.de", "123", "Max", "Mustermann");
    	User user = User.createUser(
    			registration.getUsername(),
    			registration.getEmail(),
				registration.getPassword(), 
				registration.getFirstName(), 
				registration.getLastName());
    	
    	
    	accountService.createAccount(user);
   
    	return new Message(true, "The Users was created.");
     
    }
    
    @RequestMapping("/login/account")
    public Account test2(@RequestParam(value="name", defaultValue="World") String name) {
        return new Account("Hans", "Franz","Hansi","123");
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
