package com.pack.book.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import com.pack.book.dto.UserDTO;
import com.pack.book.entities.User;
import com.pack.book.exception.BookingManagementException;
import com.pack.book.repository.UserRepository;
import com.pack.book.service.UserService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserDTO userDTO;
    private User user;
    private UserDTO conflictingUserDTO;
    private User conflictingUser;

    @BeforeEach
    void setUp() {
        initializeUserObjects();
    }

    private void initializeUserObjects() {
        userDTO = new UserDTO();
        userDTO.setName("Manoj Sharma");
        userDTO.setDate(LocalDate.now().toString());
        userDTO.setStartTime("10");
        userDTO.setEndTime("12");
        userDTO.setStatus("Approved");
        userDTO.setConflictStatus("Non-Conflict");

        user = new User();
        user.setId(1L);
        user.setName("Manoj Sharma");
        user.setDate(LocalDate.now().toString());
        user.setStartTime("10");
        user.setEndTime("12");
        user.setStatus("Approved");
        user.setConflictStatus("Non-Conflict");

        conflictingUserDTO = new UserDTO();
        conflictingUserDTO.setName("Conflicting User");
        conflictingUserDTO.setDate(LocalDate.now().toString());
        conflictingUserDTO.setStartTime("11");
        conflictingUserDTO.setEndTime("13");
        conflictingUserDTO.setStatus("Approved");
        conflictingUserDTO.setConflictStatus("Non-Conflict");

        conflictingUser = new User();
        conflictingUser.setId(2L);
        conflictingUser.setName("Conflicting User");
        conflictingUser.setDate(LocalDate.now().toString());
        conflictingUser.setStartTime("11");
        conflictingUser.setEndTime("13");
        conflictingUser.setStatus("Approved");
        conflictingUser.setConflictStatus("Non-Conflict");
    }

    @Test
    void addUserWhenNameIsEmpty() {
        userDTO.setName("");

        BookingManagementException exception = assertThrows(BookingManagementException.class, () -> {
            userServiceImpl.addUser(userDTO);
        });

        assertEquals("Name field must not be empty.", exception.getMessage());
    }

    @Test
    void addUserWhenDateIsBeforeToday() {
        userDTO.setDate(LocalDate.now().minusDays(1).toString());

        BookingManagementException exception = assertThrows(BookingManagementException.class, () -> {
            userServiceImpl.addUser(userDTO);
        });

        assertEquals("Invalid date. The date must be today or a future date.", exception.getMessage());
    }

    @Test
    void addUserWhenStartTimeIsInvalid() {
        userDTO.setStartTime("25");

        BookingManagementException exception = assertThrows(BookingManagementException.class, () -> {
            userServiceImpl.addUser(userDTO);
        });
 
        assertEquals("Invalid start or end time. Please provide valid 24-hour format times.", exception.getMessage());
    }

    @Test
    void addUserWhenEndTimeIsBeforeStartTime() {
        userDTO.setEndTime("08");

        BookingManagementException exception = assertThrows(BookingManagementException.class, () -> {
            userServiceImpl.addUser(userDTO);
        });

        assertEquals("Invalid start or end time. Please provide valid 24-hour format times.", exception.getMessage());
    }
 
   /* @Test
    void addUserWhenTimeConflictExists() {
        when(modelMapper.map(any(UserDTO.class), eq(User.class))).thenReturn(user);
        when(userRepository.findAll()).thenReturn(List.of(conflictingUser));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(userDTO);

        UserDTO result = userServiceImpl.addUser(userDTO);

        assertNotNull(result);
        assertEquals("Non-Conflict", result.getConflictStatus());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).save(conflictingUser);
    }*/

    
    @ParameterizedTest
    @CsvSource({
    	 "3, Ravi Sharma, 2024-07-19, 19, 20, Approved, Non-Conflict",
    	    "4, Manoj Sharma, 2024-07-19, 19, 20, Delete, Conflict",
    	    "5, Ravan Sharma, 2024-07-20, 19, 20, Rejected, Non-Conflict",
    	    "6, Rajesh Sharma, 2024-07-19, 1, 23, Delete, Conflict",
    	    "7, Sita Sharma, 2024-07-21, 10, 20, Approved, Non-Conflict",
    	    "8, Geeta Sharma, 2024-07-22, 14, 18, Approved, Conflict",
    	    "9, Ram Sharma, 2024-07-23, 16, 19, Rejected, Non-Conflict",
    	    "10, Mohan Sharma, 2024-07-24, 8, 17, Approved, Conflict",
    	    "11, Rohan Sharma, 2024-07-25, 12, 20, Delete, Non-Conflict",
    	    "12, Mohini Sharma, 2024-07-26, 11, 15, Approved, Conflict"	
    })
    void addUsers(Long id, String name, String date, String startTime, String endTime, String status, String conflictStatus) {
        UserDTO validUserDTO = new UserDTO();
        validUserDTO.setName(name);
        validUserDTO.setDate(date);
        validUserDTO.setStartTime(startTime);
        validUserDTO.setEndTime(endTime);
        validUserDTO.setStatus(status);
        validUserDTO.setConflictStatus(conflictStatus);

        User validUser = new User();
        validUser.setName(validUserDTO.getName());
        validUser.setDate(validUserDTO.getDate());
        validUser.setStartTime(validUserDTO.getStartTime());
        validUser.setEndTime(validUserDTO.getEndTime());
        validUser.setStatus(validUserDTO.getStatus());
        validUser.setConflictStatus(validUserDTO.getConflictStatus());

        when(modelMapper.map(any(UserDTO.class), eq(User.class))).thenReturn(validUser);
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(validUserDTO);

        UserDTO result = userServiceImpl.addUser(validUserDTO);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(date, result.getDate());
        assertEquals(startTime, result.getStartTime());
        assertEquals(endTime, result.getEndTime());
        assertEquals(status, result.getStatus());
        assertEquals(conflictStatus, result.getConflictStatus());
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void addUserWhenNoConflict() {
        when(modelMapper.map(any(UserDTO.class), eq(User.class))).thenReturn(user);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(userDTO);

        UserDTO result = userServiceImpl.addUser(userDTO);

        assertNotNull(result);
        assertEquals("Non-Conflict", result.getConflictStatus());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userServiceImpl.updateUser(1L, userDTO);
        });

        assertEquals("User not found", exception.getMessage());
    }

   /* @Test
    void updateUserWithConflicts() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated User");
        updatedUser.setDate(LocalDate.now().toString());
        updatedUser.setStartTime("09");
        updatedUser.setEndTime("11");
        updatedUser.setStatus("Approved");
        updatedUser.setConflictStatus("Non-Conflict");

        List<User> users = List.of(conflictingUser);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(users);
        when(modelMapper.map(any(UserDTO.class), eq(User.class))).thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(userDTO);


        UserDTO result = userServiceImpl.updateUser(1L, userDTO);

        assertNotNull(result);
        assertEquals("Non-Conflict", result.getConflictStatus());
        verify(userRepository, times(1)).save(updatedUser);
        verify(userRepository, times(1)).save(conflictingUser);
    }*/

    @Test
    void deleteUser() {
        doNothing().when(userRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> {
            userServiceImpl.deleteUser(1L);
        });

        verify(userRepository, times(1)).deleteById(1L);
    }
}
