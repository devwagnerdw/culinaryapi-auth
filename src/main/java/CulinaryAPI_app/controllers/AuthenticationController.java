package CulinaryAPI_app.controllers;

import CulinaryAPI_app.dtos.JwtDto;
import CulinaryAPI_app.dtos.LoginDto;
import CulinaryAPI_app.dtos.UserDto;
import CulinaryAPI_app.configs.security.JwtProvider;
import CulinaryAPI_app.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger log = LogManager.getLogger(AuthenticationController.class);

    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthenticationController(JwtProvider jwtProvider, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody @Validated(UserDto.UserView.RegistrationPost.class)
                                               @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto) {
        log.debug("POST registerUser userDto received: {}", userDto);
        return userService.registerUser(userDto);
    }

    @PostMapping("/signup/admin/usr")
    public ResponseEntity<Object>registerAdmin(@RequestBody @Validated(UserDto.UserView.RegistrationPost.class)
                                                       @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto){
        return  userService.registerAdmin(userDto);
    }

    @PostMapping("/signup/delivery")
    public ResponseEntity<Object>registerDeliveryMan(@RequestBody @Validated(UserDto.UserView.RegistrationPost.class)
                                               @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto){
        return  userService.registerDeliveryMan(userDto);
    }



    @PostMapping("/login")
    public ResponseEntity<JwtDto> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        log.info("POST authenticateUser attempt for username: {}", loginDto.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generateJwt(authentication);

            log.info("User {} authenticated successfully", loginDto.getUsername());
            return ResponseEntity.ok(new JwtDto(jwt));
    }
}