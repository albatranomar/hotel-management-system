package com.hotel.management.application.service.impl;

import com.hotel.management.application.dto.UserDto;
import com.hotel.management.application.entity.Booking;
import com.hotel.management.application.entity.user.Role;
import com.hotel.management.application.entity.user.User;
import com.hotel.management.application.exception.BadRequestException;
import com.hotel.management.application.exception.ResourceNotFoundException;
import com.hotel.management.application.repository.UserRepository;
import com.hotel.management.application.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserServiceImpl::mapToDto).toList();
    }

    @Override
    public List<UserDto> getAllCustomers() {
        List<User> users = userRepository.findByRole(Role.CUSTOMER);
        return users.stream().map(UserServiceImpl::mapToDto).toList();
    }

    @Override
    public List<UserDto> getAllAdmins() {
        List<User> users = userRepository.findByRole(Role.ADMIN);
        return users.stream().map(UserServiceImpl::mapToDto).toList();
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        return mapToDto(userRepository.save(mapToEntity(userDto)));
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        try {
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            user.setPhoneNumber(userDto.getPhoneNumber());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid value provided.");
        }

        return mapToDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<Booking> getUserBookings(String id) {
        return userRepository.getBookings(id);
    }

    @Override
    public boolean hasBooking(String userId, String bookingId) {
        return userRepository.getBookings(userId).stream().anyMatch(b -> b.getId().equals(bookingId));
    }

    public static UserDto mapToDto(User user) {
        if (user == null) return null;
        return new UserDto(user.getId(), user.getRole(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPassword(), user.getPhoneNumber());
    }

    public static User mapToEntity(UserDto userDto) {
        if (userDto == null) return null;
        return new User(userDto.getId(), userDto.getRole(), userDto.getFirstName(), userDto.getLastName(),
                userDto.getEmail(), userDto.getPassword(), userDto.getPhoneNumber());
    }
}
