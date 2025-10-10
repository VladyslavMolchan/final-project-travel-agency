package com.epam.finaltask.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ThrowsUnsupportedOperationException() {
        UserDTO userDTO = new UserDTO();
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            userService.register(userDTO);
        });
        assertEquals("Not implemented", exception.getMessage());
    }

    @Test
    void updateUser_ThrowsUnsupportedOperationException() {
        UserDTO userDTO = new UserDTO();
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            userService.updateUser("username", userDTO);
        });
        assertEquals("Not implemented", exception.getMessage());
    }

    @Test
    void getUserByUsername_UserExists_ReturnsUserDTO() {
        String username = "testUser";
        User user = new User();
        UserDTO userDTO = new UserDTO();

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserByUsername(username);

        assertNotNull(result);
        assertEquals(userDTO, result);
        verify(userRepository).findUserByUsername(username);
        verify(userMapper).toUserDTO(user);
    }

    @Test
    void getUserByUsername_UserNotFound_ThrowsRuntimeException() {
        String username = "missingUser";
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername(username);
        });

        assertTrue(exception.getMessage().contains("User not found with username"));
        verify(userRepository).findUserByUsername(username);
    }

    @Test
    void changeAccountStatus_UserExists_ChangesStatusAndReturnsUserDTO() {
        UUID userId = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId.toString());
        userDTO.setActive(true);

        User userFromDto = new User();
        userFromDto.setActive(true);

        User userFromRepo = new User();
        userFromRepo.setActive(false);

        User savedUser = new User();
        savedUser.setActive(true);

        UserDTO savedUserDTO = new UserDTO();
        savedUserDTO.setActive(true);

        when(userMapper.toUser(userDTO)).thenReturn(userFromDto);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userFromRepo));
        when(userRepository.save(userFromRepo)).thenReturn(savedUser);
        when(userMapper.toUserDTO(savedUser)).thenReturn(savedUserDTO);

        UserDTO result = userService.changeAccountStatus(userDTO);

        assertNotNull(result);
        assertTrue(result.isActive());
        assertEquals(savedUserDTO, result);

        verify(userMapper).toUser(userDTO);
        verify(userRepository).findById(userId);
        verify(userRepository).save(userFromRepo);
        verify(userMapper).toUserDTO(savedUser);
    }

    @Test
    void changeAccountStatus_UserNotFound_ThrowsRuntimeException() {
        UUID userId = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId.toString());

        when(userMapper.toUser(userDTO)).thenReturn(new User());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changeAccountStatus(userDTO);
        });

        assertTrue(exception.getMessage().contains("User not found with ID"));
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_UserExists_ReturnsUserDTO() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        UserDTO userDTO = new UserDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userDTO, result);

        verify(userRepository).findById(userId);
        verify(userMapper).toUserDTO(user);
    }

    @Test
    void getUserById_UserNotFound_ThrowsRuntimeException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        });

        assertTrue(exception.getMessage().contains("User not found with ID"));
        verify(userRepository).findById(userId);
    }
}
