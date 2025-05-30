package CulinaryAPI_app.services.impl;

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
import CulinaryAPI_app.services.RoleService;
import CulinaryAPI_app.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;
    private final DeliverymanEventPublisher deliverymanEventPublisher;
    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository,
                           UserEventPublisher userEventPublisher, DeliverymanEventPublisher deliverymanEventPublisher, RoleService roleService) {
        this.passwordEncoder = passwordEncoder;
        this.userEventPublisher = userEventPublisher;
        this.userRepository = userRepository;
        this.deliverymanEventPublisher = deliverymanEventPublisher;
        this.roleService = roleService;
    }

    @Transactional
    @Override
    public ResponseEntity<Object> registerUser(UserDto userDto) {
        LOGGER.info("Starting user registration for username: {}", userDto.getUsername());

        if (userRepository.existsByUsername(userDto.getUsername())) {
            LOGGER.warn("Username already taken: {}", userDto.getUsername());
            throw new BusinessException("Error: Username is already taken: " + userDto.getUsername());
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            LOGGER.warn("Email already taken: {}", userDto.getEmail());
            throw new BusinessException("Error: Email is already taken: " + userDto.getEmail());
        }

        RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_CUSTOMER)
                .orElseThrow(() -> new BusinessException("Error: Role is not found."));

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.CUSTOMER);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.getRoles().add(roleModel);

        userRepository.save(userModel);
        LOGGER.info("User {} registered successfully.", userDto.getUsername());

        userEventPublisher.publishUserEvent(userModel.convertToUserEventDto(), ActionType.CREATE);
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @Override
    public ResponseEntity<Object> registerAdmin(UserDto userDto) {
        LOGGER.info("Starting user registration for username: {}", userDto.getUsername());

        if (userRepository.existsByUsername(userDto.getUsername())) {
            LOGGER.warn("Username already taken: {}", userDto.getUsername());
            throw new BusinessException("Error: Username is already taken: " + userDto.getUsername());
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            LOGGER.warn("Email already taken: {}", userDto.getEmail());
            throw new BusinessException("Error: Email is already taken: " + userDto.getEmail());
        }

        RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new BusinessException("Error: Role is not found."));

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.ADMIN);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.getRoles().add(roleModel);

        userRepository.save(userModel);
        LOGGER.info("Admin {} registered successfully.", userDto.getUsername());

        userEventPublisher.publishUserEvent(userModel.convertToUserEventDto(), ActionType.CREATE);
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @Override
    public ResponseEntity<Object> registerDeliveryMan(UserDto userDto) {
        LOGGER.info("Starting user registration for username: {}", userDto.getUsername());

        if (userRepository.existsByUsername(userDto.getUsername())) {
            LOGGER.warn("Username already taken: {}", userDto.getUsername());
            throw new BusinessException("Error: Username is already taken: " + userDto.getUsername());
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            LOGGER.warn("Email already taken: {}", userDto.getEmail());
            throw new BusinessException("Error: Email is already taken: " + userDto.getEmail());
        }

        RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_DELIVERY)
                .orElseThrow(() -> new BusinessException("Error: Role is not found."));

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.DELIVERY);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.getRoles().add(roleModel);

        userRepository.save(userModel);
        LOGGER.info("DeliveryMan {} registered successfully.", userDto.getUsername());

        deliverymanEventPublisher.publishDeliverymanEvent(userModel.convertToUserEventDto(),ActionType.CREATE);
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);

    }

    @Override
    public ResponseEntity<Object> registerChef(UserDto userDto) {
        LOGGER.info("Starting user registration for username: {}", userDto.getUsername());

        if (userRepository.existsByUsername(userDto.getUsername())) {
            LOGGER.warn("Username already taken: {}", userDto.getUsername());
            throw new BusinessException("Error: Username is already taken: " + userDto.getUsername());
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            LOGGER.warn("Email already taken: {}", userDto.getEmail());
            throw new BusinessException("Error: Email is already taken: " + userDto.getEmail());
        }

        RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_CHEF)
                .orElseThrow(() -> new BusinessException("Error: Role is not found."));

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.DELIVERY);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.getRoles().add(roleModel);

        userRepository.save(userModel);
        LOGGER.info("Chef {} registered successfully.", userDto.getUsername());

        deliverymanEventPublisher.publishDeliverymanEvent(userModel.convertToUserEventDto(),ActionType.CREATE);
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);

    }

    @Override
    public ResponseEntity<Object> getOneUser(UUID userId) {
        LOGGER.info("Fetching user with ID: {}", userId);
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.warn("User not found: {}", userId);
                    return new NotFoundException("User not found: " + userId);
                });

        LOGGER.info("User found: {}", userModel.getUsername());
        return ResponseEntity.ok(userModel);
    }

    @Transactional
    @Override
    public ResponseEntity<Object> deactivateUser(UUID userId) {
        LOGGER.info("Deactivating user with ID: {}", userId);
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.warn("User not found: {}", userId);
                    return new NotFoundException("User not found: " + userId);
                });

        userModel.setUserStatus(UserStatus.BLOCKED);
        userRepository.save(userModel);
        userEventPublisher.publishUserEvent(userModel.convertToUserEventDto(), ActionType.UPDATE);

        LOGGER.info("User {} blocked successfully.", userModel.getUsername());
        return ResponseEntity.ok("User blocked successfully.");
    }

    @Transactional
    @Override
    public ResponseEntity<Object> updateUser(UUID userId, UserDto userDto) {
        LOGGER.info("Updating user with ID: {}", userId);
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.warn("User not found: {}", userId);
                    return new NotFoundException("User not found: " + userId);
                });

        userModel.setFullName(userDto.getFullName());
        userModel.setPhoneNumber(userDto.getPhoneNumber());
        userModel.setCpf(userDto.getCpf());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userEventPublisher.publishUserEvent(userModel.convertToUserEventDto(), ActionType.UPDATE);
        userRepository.save(userModel);

        LOGGER.info("User {} updated successfully.", userModel.getUsername());
        return ResponseEntity.ok(userModel);
    }

    @Override
    public ResponseEntity<Object> updatePassword(UUID userId, UserDto userDto) {
        LOGGER.info("Updating password for user ID: {}", userId);
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.warn("User not found: {}", userId);
                    return new NotFoundException("User not found: " + userId);
                });

        if (!passwordEncoder.matches(userDto.getOldPassword(), userModel.getPassword())) {
            LOGGER.warn("Mismatched old password for user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password!");
        }

        userModel.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userRepository.save(userModel);

        LOGGER.info("Password updated successfully for user ID: {}", userId);
        return ResponseEntity.ok("Password updated successfully.");
    }

    @Override
    public ResponseEntity<Object> updateImage(UUID userId, UserDto userDto) {
        LOGGER.info("Updating image for user ID: {}", userId);
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.warn("User not found: {}", userId);
                    return new NotFoundException("User not found: " + userId);
                });

        userModel.setImageUrl(userDto.getImageUrl());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userRepository.save(userModel);

        LOGGER.info("Image updated successfully for user ID: {}", userId);
        return ResponseEntity.ok(userModel);
    }

    @Override
    public Page<UserModel> findAll(Pageable pageable) {
        LOGGER.info("Fetching all users with pagination.");
        return userRepository.findAll(pageable);
    }
}
