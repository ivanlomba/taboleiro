package taboleiro.model.service.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderGenerator {

    public static PasswordEncoder encoder(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

    public static String Encode(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        return hashedPassword;
    }
}
