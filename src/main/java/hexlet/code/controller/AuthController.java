package hexlet.code.controller;

import hexlet.code.dto.AuthDTO;
import hexlet.code.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(@RequestBody AuthDTO authDTO) {
        var authentication = new UsernamePasswordAuthenticationToken(
                authDTO.getUsername(), authDTO.getPassword()
        );
        authenticationManager.authenticate(authentication);
        return jwtUtils.generateToken(authDTO.getUsername());
    }
}
