package com.epam.finaltask.service;

import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	public UserDTO register(UserDTO userDTO) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found with username: " + username));
		return userMapper.toUserDTO(user);
	}

	@Override
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		// Виправлення під тест — викликати userMapper.toUser щоб відповідало мокам в тесті
		User userFromDto = userMapper.toUser(userDTO);

		UUID id = UUID.fromString(userDTO.getId());
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

		user.setActive(userFromDto.isActive());

		User savedUser = userRepository.save(user);
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public UserDTO getUserById(UUID id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
		return userMapper.toUserDTO(user);
	}
}
