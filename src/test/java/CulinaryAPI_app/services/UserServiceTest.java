package CulinaryAPI_app.services;

import CulinaryAPI_app.dtos.UserDto;
import CulinaryAPI_app.enums.ActionType;
import CulinaryAPI_app.enums.RoleType;
import CulinaryAPI_app.enums.UserStatus;
import CulinaryAPI_app.enums.UserType;
import CulinaryAPI_app.exception.BusinessException;
import CulinaryAPI_app.exception.NotFoundException;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
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
    @DisplayName("User Registration Tests")
    class RegistrationTests {

        @Nested
        @DisplayName("Successful Registrations")
        class SuccessfulRegistrations {
            @Test
            @DisplayName("Should register customer successfully")
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
        @DisplayName("Registration Failures")
        class RegistrationFailures {
            @Test
            @DisplayName("Should fail when role not found")
            void registerUser_RoleNotFound() {
                when(userRepository.existsByUsername(anyString())).thenReturn(false);
                when(userRepository.existsByEmail(anyString())).thenReturn(false);
                when(roleService.findByRoleName(RoleType.ROLE_CUSTOMER)).thenReturn(Optional.empty());

                BusinessException exception = assertThrows(BusinessException.class,
                        () -> userService.registerUser(validUserDto));

                assertEquals("Error: Role is not found.", exception.getMessage());
            }

            @Test
            @DisplayName("Should fail when username exists")
            void registerUser_UsernameExists() {
                when(userRepository.existsByUsername(validUserDto.getUsername())).thenReturn(true);

                BusinessException exception = assertThrows(BusinessException.class,
                        () -> userService.registerUser(validUserDto));

                assertEquals("Error: Username is already taken: test", exception.getMessage());
                verify(userRepository, never()).save(any());
                verify(userEventPublisher, never()).publishUserEvent(any(), any());
            }

            @Test
            @DisplayName("Should fail when email exists")
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
    }

    @Nested
    @DisplayName("User Image Update Tests")
    class ImageUpdateTests {

        @Test
        @DisplayName("Should successfully update user image URL")
        void updateImage_Success() {
            UUID userId = UUID.randomUUID();
            String newImageUrl = "https://example.com/new-image.jpg";

            UserModel existingUser = new UserModel();
            existingUser.setUserId(userId);
            existingUser.setImageUrl("https://example.com/old-image.jpg");

            UserDto updateRequest = new UserDto();
            updateRequest.setImageUrl(newImageUrl);

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ResponseEntity<Object> response = userService.updateImage(userId, updateRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertInstanceOf(UserModel.class, response.getBody());

            UserModel updatedUser = (UserModel) response.getBody();
            assertEquals(newImageUrl, updatedUser.getImageUrl());
            assertNotNull(updatedUser.getLastUpdateDate());

            verify(userRepository).findById(userId);
            verify(userRepository).save(existingUser);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when user doesn't exist")
        void updateImage_UserNotFound() {
            UUID userId = UUID.randomUUID();
            UserDto updateRequest = new UserDto();
            updateRequest.setImageUrl("https://example.com/new-image.jpg");

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> userService.updateImage(userId, updateRequest));

            assertEquals("User not found: " + userId, exception.getMessage());
            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(userEventPublisher);
        }

        @Test
        @DisplayName("Should update lastUpdateDate when changing image")
        void updateImage_UpdatesTimestamp() {
            // Arrange
            UUID userId = UUID.randomUUID();
            LocalDateTime originalDate = LocalDateTime.now(ZoneId.of("UTC")).minusDays(1);

            UserModel existingUser = new UserModel();
            existingUser.setUserId(userId);
            existingUser.setImageUrl("old-url");
            existingUser.setLastUpdateDate(originalDate);

            UserDto updateRequest = new UserDto();
            updateRequest.setImageUrl("new-url");

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ResponseEntity<Object> response = userService.updateImage(userId, updateRequest);

            UserModel updatedUser = (UserModel) response.getBody();
            assertNotNull(updatedUser.getLastUpdateDate());
            assertTrue(updatedUser.getLastUpdateDate().isAfter(originalDate));
        }

        @Test
        @DisplayName("Should handle null image URL by setting it to null")
        void updateImage_NullUrl() {
            UUID userId = UUID.randomUUID();

            UserModel existingUser = new UserModel();
            existingUser.setUserId(userId);
            existingUser.setImageUrl("old-url");

            UserDto updateRequest = new UserDto();
            updateRequest.setImageUrl(null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ResponseEntity<Object> response = userService.updateImage(userId, updateRequest);

            UserModel updatedUser = (UserModel) response.getBody();
            assertNull(updatedUser.getImageUrl());
        }
    }

    @Nested
    @DisplayName("User Retrieval Tests")
    class RetrievalTests {
        @Test
        @DisplayName("Should successfully retrieve user by ID")
        void getOneUser_Success() {
            UUID userId = UUID.randomUUID();
            UserModel userModel = new UserModel();
            userModel.setUserId(userId);
            userModel.setUsername("testUser");
            userModel.setEmail("test@example.com");

            when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));

            ResponseEntity<Object> response = userService.getOneUser(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertInstanceOf(UserModel.class, response.getBody());

            UserModel returnedUser = (UserModel) response.getBody();
            assertEquals(userId, returnedUser.getUserId());
            assertEquals("testUser", returnedUser.getUsername());
            assertEquals("test@example.com", returnedUser.getEmail());
        }

        @Test
        @DisplayName("Should fail when user not found")
        void getOneUser_NotFound() {
            UUID userId = UUID.randomUUID();

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getOneUser(userId));

            assertEquals("User not found: " + userId, exception.getMessage());
            verify(userRepository).findById(userId);
        }
    }

    @Nested
    @DisplayName("User Management Tests")
    class ManagementTests {
        @Test
        @DisplayName("Should successfully deactivate user")
        void deactivateUser_success() {
            UUID userId = UUID.randomUUID();
            UserModel userModel = new UserModel();
            userModel.setUserId(userId);
            userModel.setUserStatus(UserStatus.ACTIVE);

            when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));

            ResponseEntity<Object> response = userService.deactivateUser(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertInstanceOf(String.class, response.getBody());
            assertEquals("User blocked successfully.", response.getBody());
        }

        @Test
        @DisplayName("Should successfully update user details")
        void updateUser_success() {
            UUID userId = UUID.randomUUID();
            UserModel userModel = new UserModel();
            userModel.setUserId(userId);
            userModel.setUserStatus(UserStatus.ACTIVE);
            userModel.setFullName("vagne alves");

            UserDto userDto = new UserDto();
            userDto.setFullName("vagner alves");

            when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));

            ResponseEntity<Object> response = userService.updateUser(userId, userDto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(userModel.getFullName(), "vagner alves");
        }

        @Test
        @DisplayName("Should successfully update user password")
        void updatePassword_success() {
            UUID userId = UUID.randomUUID();

            String oldPassword = "123456";
            String newPassword = "1234567";
            String encodedOldPassword = "$2a$10$abcdefghijklmnopqrstuv";

            UserModel userModel = new UserModel();
            userModel.setUserId(userId);
            userModel.setPassword(encodedOldPassword);
            userModel.setUserStatus(UserStatus.ACTIVE);

            UserDto userDto = new UserDto();
            userDto.setOldPassword(oldPassword);
            userDto.setPassword(newPassword);

            when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));
            when(passwordEncoder.matches(oldPassword, encodedOldPassword)).thenReturn(true);
            when(passwordEncoder.encode(newPassword)).thenReturn("new_encoded_password");

            ResponseEntity<Object> response = userService.updatePassword(userId, userDto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("new_encoded_password", userModel.getPassword());
            assertEquals("Password updated successfully.", response.getBody());
            verify(userRepository).save(userModel);
            verify(passwordEncoder).encode(newPassword);
        }

        @Test
        @DisplayName("Update Password - wrong old password")
        void updatePassword_wrongOldPassword() {
            UUID userId = UUID.randomUUID();

            String oldPassword = "123456";
            String newPassword = "1234567";
            String encodedOldPassword = "$2a$10$abcdefghijklmnopqrstuv";

            UserModel userModel = new UserModel();
            userModel.setUserId(userId);
            userModel.setPassword(encodedOldPassword);
            userModel.setUserStatus(UserStatus.ACTIVE);

            UserDto userDto = new UserDto();
            userDto.setOldPassword(oldPassword);
            userDto.setPassword(newPassword);

            when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));
            when(passwordEncoder.matches(oldPassword, encodedOldPassword)).thenReturn(false);

            ResponseEntity<Object> response = userService.updatePassword(userId, userDto);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Error: Mismatched old password!", response.getBody());
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