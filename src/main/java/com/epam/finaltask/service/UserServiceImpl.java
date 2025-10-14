package com.epam.finaltask.service;


import java.util.UUID;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.UserNotFoundException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;




@Slf4j
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
		log.info("Attempting to register user with username: {}", userDTO.getUsername());
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		log.info("Updating user {} with new data", username);
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		log.debug("Fetching user by username: {}", username);
		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> {
					log.error("User not found with username: {}", username);
					return new UserNotFoundException("User not found with username: " + username);
				});
		return userMapper.toUserDTO(user);
	}

	@Override
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		log.info("Changing account status for user id {}", userDTO.getId());

		User userFromDto = userMapper.toUser(userDTO);

		UUID id = UUID.fromString(userDTO.getId());
		User user = userRepository.findById(id)
				.orElseThrow(() -> {
					log.error("User not found with ID: {}", id);
					return new UserNotFoundException("User not found with ID: " + id);
				});

		user.setActive(userFromDto.isActive());
		User savedUser = userRepository.save(user);

		log.info("Account status changed for user id {} → active={}", id, savedUser.isActive());
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public UserDTO getUserById(UUID id) {
		log.debug("Fetching user by id {}", id);
		User user = userRepository.findById(id)
				.orElseThrow(() -> {
					log.error("User not found with ID: {}", id);
					return new UserNotFoundException("User not found with ID: " + id);
				});
		return userMapper.toUserDTO(user);
	}
}
