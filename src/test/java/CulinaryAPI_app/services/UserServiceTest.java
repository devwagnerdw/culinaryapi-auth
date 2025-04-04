package CulinaryAPI_app.services;

import CulinaryAPI_app.dtos.UserDto;
import CulinaryAPI_app.dtos.UserEventDto;
import CulinaryAPI_app.enums.ActionType;
import CulinaryAPI_app.enums.RoleType;
import CulinaryAPI_app.enums.UserStatus;
import CulinaryAPI_app.enums.UserType;
import CulinaryAPI_app.exception.BusinessException;
import CulinaryAPI_app.models.RoleModel;
import CulinaryAPI_app.models.UserModel;
import CulinaryAPI_app.publishers.DeliverymanEventPublisher;
import CulinaryAPI_app.publishers.UserEventPublisher;
import CulinaryAPI_app.repositories.UserRepository;
import CulinaryAPI_app.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleService roleService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserEventPublisher userEventPublisher;
    @Mock private DeliverymanEventPublisher deliverymanEventPublisher;

    @InjectMocks private UserServiceImpl userService;

    private UserDto validUserDto;
    private RoleModel customerRole;
    private RoleModel adminRole;
    private RoleModel deliveryRole;

    @BeforeEach
    void setUp() {
        validUserDto = new UserDto();
        validUserDto.setUsername("test");
        validUserDto.setEmail("test@example.com");
        validUserDto.setPassword("password123");
        validUserDto.setFullName("Test User");

        customerRole = new RoleModel();
        customerRole.setRoleName(RoleType.ROLE_CUSTOMER);

        adminRole = new RoleModel();
        adminRole.setRoleName(RoleType.ROLE_ADMIN);

        deliveryRole = new RoleModel();
        deliveryRole.setRoleName(RoleType.ROLE_DELIVERY);
    }

    @Nested
    @DisplayName("Register User Tests")
    class RegisterUserTests {
        @Test
        @DisplayName("Should register user successfully")
        void registerUser_Success() {
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleService.findByRoleName(RoleType.ROLE_CUSTOMER)).thenReturn(Optional.of(customerRole));
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ResponseEntity<Object> response = userService.registerUser(validUserDto);

            assertSuccessfulRegistration(response, UserType.CUSTOMER);
            verify(userEventPublisher).publishUserEvent(any(), eq(ActionType.CREATE));
        }

        @Test
        @DisplayName("Should throw exception when role not found")
        void registerUser_RoleNotFound() {
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleService.findByRoleName(RoleType.ROLE_CUSTOMER)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.registerUser(validUserDto));

            assertEquals("Error: Role is not found.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Register Admin Tests")
    class RegisterAdminTests {
        @Test
        @DisplayName("Should register admin successfully")
        void registerAdmin_Success() {
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleService.findByRoleName(RoleType.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ResponseEntity<Object> response = userService.registerAdmin(validUserDto);

            assertSuccessfulRegistration(response, UserType.ADMIN);
            verify(userEventPublisher).publishUserEvent(any(), eq(ActionType.CREATE));
        }
    }

    @Nested
    @DisplayName("Register Deliveryman Tests")
    class RegisterDeliverymanTests {
        @Test
        @DisplayName("Should register deliveryman successfully")
        void registerDeliveryMan_Success() {
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleService.findByRoleName(RoleType.ROLE_DELIVERY)).thenReturn(Optional.of(deliveryRole));
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ResponseEntity<Object> response = userService.registerDeliveryMan(validUserDto);

            assertSuccessfulRegistration(response, UserType.DELIVERY);
            verify(deliverymanEventPublisher).publishDeliverymanEvent(any(), eq(ActionType.CREATE));
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {
        @Test
        @DisplayName("Should throw exception when username exists")
        void registerUser_UsernameExists() {
            when(userRepository.existsByUsername(validUserDto.getUsername())).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.registerUser(validUserDto));

            assertEquals("Error: Username is already taken: test", exception.getMessage());
            verify(userRepository, never()).save(any());
            verify(userEventPublisher, never()).publishUserEvent(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when email exists")
        void registerUser_EmailExists() {
            when(userRepository.existsByUsername(validUserDto.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(validUserDto.getEmail())).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.registerUser(validUserDto));

            assertEquals("Error: Email is already taken: test@example.com", exception.getMessage());
            verify(userRepository, never()).save(any());
            verify(userEventPublisher, never()).publishUserEvent(any(), any());
        }
    }

    private void assertSuccessfulRegistration(ResponseEntity<Object> response, UserType expectedUserType) {
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(UserModel.class, response.getBody());

        UserModel savedUser = (UserModel) response.getBody();
        assertEquals("test", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(UserStatus.ACTIVE, savedUser.getUserStatus());
        assertEquals(expectedUserType, savedUser.getUserType());
        assertNotNull(savedUser.getCreationDate());
        assertNotNull(savedUser.getLastUpdateDate());
        assertEquals(savedUser.getCreationDate(), savedUser.getLastUpdateDate());
    }
}