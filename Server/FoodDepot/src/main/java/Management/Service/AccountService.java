package Management.Service;

import Management.Model.Login.User;

public interface AccountService {
	
	public boolean createAccount(Management.Model.Login.User account);
	
	public User findByEmail(String email);
	
	public User findByID(String id);
	
	public boolean changeUserPassword(String email, String oldPassword, String newPassword);
}
