package CulinaryAPI_app.controllers;

import CulinaryAPI_app.dtos.JwtDto;
import CulinaryAPI_app.dtos.LoginDto;
import CulinaryAPI_app.dtos.UserDto;
import CulinaryAPI_app.configs.security.JwtProvider;
import CulinaryAPI_app.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Salvar um usuário", description = "Cria um novo usuário com as informações fornecidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody @Validated(UserDto.UserView.RegistrationPost.class)
                                               @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto) {
        log.debug("POST registerUser userDto received: {}", userDto);
        return userService.registerUser(userDto);
    }

    @Operation(summary = "Salvar um Admin", description = "Cria um novo Admin com as informações fornecidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/signup/admin/usr")
    public ResponseEntity<Object>registerAdmin(@RequestBody @Validated(UserDto.UserView.RegistrationPost.class)
                                                       @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto){
        return  userService.registerAdmin(userDto);
    }


    @Operation(summary = "Salvar um Entregador", description = "Cria um novo Entregador com as informações fornecidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entregador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/signup/deliveryman")
    public ResponseEntity<Object>registerDeliveryMan(@RequestBody @Validated(UserDto.UserView.RegistrationPost.class)
                                               @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto){
        return  userService.registerDeliveryMan(userDto);
    }


    @Operation(summary = "Salvar um Chef", description = "Cria um novo Chef com as informações fornecidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chef criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/signup/chef")
    public ResponseEntity<Object>registerChef(@RequestBody @Validated(UserDto.UserView.RegistrationPost.class)
                                                     @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto){
        return  userService.registerChef(userDto);
    }



    @Operation(summary = "Login de Usuário", description = "Autentica um usuário e gera um token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token JWT gerado"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
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