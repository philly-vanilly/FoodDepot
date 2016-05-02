package Management.Model.Login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordCrypto {

    private static PasswordEncoder passwordEncoder;

    private static PasswordCrypto instance;

    public static PasswordCrypto getInstance() {
        if(instance == null) {
            instance = new PasswordCrypto();
            passwordEncoder = new BCryptPasswordEncoder();
            
        }

        return instance;
    }

    public String encrypt(String str) {
        return passwordEncoder.encode(str);
    }
}