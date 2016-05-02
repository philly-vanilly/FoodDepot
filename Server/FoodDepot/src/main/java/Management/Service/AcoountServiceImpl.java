package Management.Service;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import Management.Model.Login.UserRole;

@Service
public class AcoountServiceImpl implements AccountService, UserDetailsService {
	
	@Autowired  
	private MongoOperations operations; 
	
	@Override
	public boolean createAccount(Management.Model.Login.User user) {
		operations.save(user, "users");
		//operations.sav
		return true;
	}
	
	

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

    	Management.Model.Login.User user = this.findByEmail(username);
    	if(user == null){
    		throw new UsernameNotFoundException("The username doesn't exist");
    	}
        List<GrantedAuthority> authorities = buildUserAuthority(user.getRoles());

        return buildUserForAuthentication(user, authorities);

    }

    private User buildUserForAuthentication(Management.Model.Login.User user,
                                            List<GrantedAuthority> authorities) {
        return new User(user.getId(), user.getPassword(), authorities);
    }

    private List<GrantedAuthority> buildUserAuthority(Set<UserRole> userRoles) {

        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

        // Build user's authorities
        for (UserRole userRole : userRoles) {
            setAuths.add(new SimpleGrantedAuthority(userRole.getRoleName()));
        }

        return new ArrayList<GrantedAuthority>(setAuths);
    }
    
    private Management.Model.Login.User findByEmail(String email){
    	Criteria emailCrit = Criteria.where("email").is(email);
    	System.out.println("Test");
    	Management.Model.Login.User user =  operations.findOne(new Query(emailCrit),Management.Model.Login.User.class, "users");
    	System.out.println(user.getEmail());
    	return user;
    	//TASK
    }

}
