package Management.Service;

import java.util.ArrayList;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;

import Management.Model.Login.UserRole;

@Service
public class AcoountServiceImpl implements AccountService, UserDetailsService {
	
	@Autowired  
	private MongoOperations operations; 
	
	@Override
	public boolean createAccount(Management.Model.Login.User user) {
		if(user != null){
			operations.save(user, "users");
			return true;
		} else {
			return false;
		}
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
    
    public Management.Model.Login.User findByEmail(String email){
    	Criteria emailCrit = Criteria.where("email").is(email);
    	Management.Model.Login.User user =  operations.findOne(new Query(emailCrit),Management.Model.Login.User.class, "users");
    	return user;
    }
    
    public Management.Model.Login.User findByID(String id){
    	Criteria idCrit = Criteria.where("_id").is(id);
    	Management.Model.Login.User user =  operations.findOne(new Query(idCrit),Management.Model.Login.User.class, "users");
    	return user;
    }




	@Override
	public boolean changeUserPassword(String email, String oldPassword, String newPassword) {
		Management.Model.Login.User user = this.findByEmail(email);
		
		if(oldPassword.equals(user.getPassword())){
			user.setPassword(newPassword);
			Query query = new Query(Criteria.where("id").is(user.getId()));
			WriteResult result = this.operations.updateFirst(query, Update.update("password", newPassword), Management.Model.Login.User.class, "users");
			return true; 
		}
			
		return false;
	}

    
    
}
