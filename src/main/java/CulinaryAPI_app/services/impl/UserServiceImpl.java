package CulinaryAPI_app.services.impl;


import CulinaryAPI_app.dtos.UserDto;
import CulinaryAPI_app.enums.ActionType;
import CulinaryAPI_app.enums.UserStatus;
import CulinaryAPI_app.exception.BusinessException;
import CulinaryAPI_app.exception.NotFoundException;
import CulinaryAPI_app.models.UserModel;
import CulinaryAPI_app.publishers.UserEventPublisher;
import CulinaryAPI_app.repositories.UserRepository;
import CulinaryAPI_app.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {


    private final UserEventPublisher userEventPublisher;
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository,UserEventPublisher userEventPublisher) {
        this.userEventPublisher=userEventPublisher;
        this.userRepository = userRepository;
    }


    @Transactional
    @Override
    public ResponseEntity<Object> registerUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new BusinessException("Error: Username is Already Taken: " + userDto.getUsername());
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BusinessException("Error: Email is Already Taken: " + userDto.getEmail());
        }

        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userRepository.save(userModel);

        userEventPublisher.publishUserEvent(userModel.convertToUserEventDto(), ActionType.CREATE);

        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @Override
    public ResponseEntity<Object> getOneUser(UUID userId) {
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        return ResponseEntity.ok(userModel);
    }

    @Transactional
    @Override
    public ResponseEntity<Object> deactivateUser(UUID userId) {
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        userModel.setUserStatus(UserStatus.BLOCKED);
        userRepository.save(userModel);
        userEventPublisher.publishUserEvent(userModel.convertToUserEventDto(), ActionType.UPDATE);
        return ResponseEntity.ok("User blocked successfully.");
    }

    @Transactional
    @Override
    public ResponseEntity<Object> updateUser(UUID userId, UserDto userDto) {

        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));


        userModel.setFullName(userDto.getFullName());
        userModel.setPhoneNumber(userDto.getPhoneNumber());
        userModel.setCpf(userDto.getCpf());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userEventPublisher.publishUserEvent(userModel.convertToUserEventDto(),ActionType.UPDATE);
        userRepository.save(userModel);
        return ResponseEntity.ok(userModel);
    }

    @Override
    public ResponseEntity<Object> updatePassword(UUID userId, UserDto userDto) {
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (!userModel.getPassword().equals(userDto.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password!");
        }
        userModel.setPassword(userDto.getPassword());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userRepository.save(userModel);
        return ResponseEntity.ok("Password updated successfully.");
    }

    @Override
    public ResponseEntity<Object> updateImage(UUID userId, UserDto userDto) {
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        userModel.setImageUrl(userDto.getImageUrl());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userRepository.save(userModel);
        return ResponseEntity.ok(userModel);

    }

    @Override
    public Page<UserModel> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

}
