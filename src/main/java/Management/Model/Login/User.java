package Management.Model.Login;

import java.util.HashSet;

import java.util.Set;

import Management.Model.AccountClient;

public class User implements AccountClient {

	private String id;

    private String username;

    private String password;

    private String email;
    
	private String firstName;
	
	private String lastName;

    private Set<UserRole> roles;

    public static User createUser(String username, String email, String password, String firstName, String lastName) {
        User user = new User();

        user.username = username;
        user.email = email;
        user.firstName = firstName;
        user.lastName = lastName;
        
        user.password = PasswordCrypto.getInstance().encrypt(password);

        if(user.roles == null) {
            user.roles = new HashSet<UserRole>();
        }

        //create a new user with basic user privileges
        user.roles.add(
                new UserRole(
                        RoleEnum.USER.toString()));

        return user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
