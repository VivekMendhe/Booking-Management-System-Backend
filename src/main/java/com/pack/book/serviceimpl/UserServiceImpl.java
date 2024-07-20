package com.pack.book.serviceimpl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pack.book.dto.UserDTO;
import com.pack.book.entities.User;
import com.pack.book.exception.BookingManagementException;
import com.pack.book.repository.UserRepository;
import com.pack.book.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired 
	private UserRepository userRepository;

	/*
	 * @Override public UserDTO addUser(UserDTO userDTO) { User user =
	 * dtoToEntity(userDTO); List<User> users = userRepository.findAll();
	 * 
	 * // Check for time conflicts boolean isConflict = users.stream().anyMatch(u ->
	 * u.getDate().equals(user.getDate()) && ((Integer.parseInt(user.getStartTime())
	 * >= Integer.parseInt(u.getStartTime()) &&
	 * Integer.parseInt(user.getStartTime()) < Integer.parseInt(u.getEndTime())) ||
	 * (Integer.parseInt(user.getEndTime()) > Integer.parseInt(u.getStartTime()) &&
	 * Integer.parseInt(user.getEndTime()) <= Integer.parseInt(u.getEndTime()))));
	 * 
	 * user.setConflictStatus(isConflict ? "Conflict" : "Non-Conflict");
	 * 
	 * User savedUser = userRepository.save(user); return entityToDTO(savedUser); }
	 */

	/*
	 * @Override public UserDTO addUser(UserDTO userDTO) {
	 * 
	 * // Validate that startTime is less than endTime int startTime =
	 * Integer.parseInt(userDTO.getStartTime()); int endTime =
	 * Integer.parseInt(userDTO.getEndTime());
	 * 
	 * if (endTime <= startTime) { throw new
	 * BookingManagementException("End time must be greater than start time."); }
	 * 
	 * User user = dtoToEntity(userDTO); List<User> users =
	 * userRepository.findAll();
	 * 
	 * // Check for time conflicts and collect conflicting users List<User>
	 * conflictingUsers = users.stream() .filter(u ->
	 * u.getDate().equals(user.getDate()) && ((Integer.parseInt(user.getStartTime())
	 * >= Integer.parseInt(u.getStartTime()) &&
	 * Integer.parseInt(user.getStartTime()) < Integer.parseInt(u.getEndTime())) ||
	 * (Integer.parseInt(user.getEndTime()) > Integer.parseInt(u.getStartTime()) &&
	 * Integer.parseInt(user.getEndTime()) <= Integer.parseInt(u.getEndTime()))))
	 * .collect(Collectors.toList());
	 * 
	 * boolean isConflict = !conflictingUsers.isEmpty();
	 * user.setConflictStatus(isConflict ? "Conflict" : "Non-Conflict");
	 * 
	 * // Update conflict status of existing conflicting users if (isConflict) { for
	 * (User conflictingUser : conflictingUsers) {
	 * conflictingUser.setConflictStatus("Conflict");
	 * userRepository.save(conflictingUser); } }
	 * 
	 * User savedUser = userRepository.save(user); return entityToDTO(savedUser); }
	 */

	/*
	 * @Override public UserDTO addUser(UserDTO userDTO) { // Validate that
	 * startTime is less than endTime int startTime =
	 * Integer.parseInt(userDTO.getStartTime()); int endTime =
	 * Integer.parseInt(userDTO.getEndTime());
	 * 
	 * if (endTime <= startTime) { throw new
	 * BookingManagementException("End time must be greater than start time."); }
	 * 
	 * User user = dtoToEntity(userDTO); List<User> users =
	 * userRepository.findAll();
	 * 
	 * // Check for time conflicts and collect conflicting users List<User>
	 * conflictingUsers = users.stream() .filter(u ->
	 * u.getDate().equals(user.getDate()) && ((startTime >=
	 * Integer.parseInt(u.getStartTime()) && startTime <
	 * Integer.parseInt(u.getEndTime())) || (endTime >
	 * Integer.parseInt(u.getStartTime()) && endTime <=
	 * Integer.parseInt(u.getEndTime()) ))) .collect(Collectors.toList());
	 * 
	 * // Set conflict status for the new user boolean isConflict =
	 * !conflictingUsers.isEmpty(); user.setConflictStatus(isConflict ? "Conflict" :
	 * "Non-Conflict");
	 * 
	 * // Save the user with conflict status User savedUser =
	 * userRepository.save(user);
	 * 
	 * // Update conflict status of conflicting users for (User conflictingUser :
	 * conflictingUsers) { conflictingUser.setConflictStatus("Conflict");
	 * userRepository.save(conflictingUser); }
	 * 
	 * // Return the saved user DTO return entityToDTO(savedUser); }
	 */

	@Override
	public UserDTO addUser(UserDTO userDTO) {

		// Validate that all required fields are not empty
		if (userDTO.getName() == null || userDTO.getName().trim().isEmpty()) {
			throw new BookingManagementException("Name field must not be empty.");
		}
		if (userDTO.getDate() == null || userDTO.getDate().trim().isEmpty()) {
			throw new BookingManagementException("Date field must not be empty.");
		}
		if (userDTO.getStartTime() == null || userDTO.getStartTime().trim().isEmpty()) {
			throw new BookingManagementException("Start time field must not be empty.");
		}
		if (userDTO.getEndTime() == null || userDTO.getEndTime().trim().isEmpty()) {
			throw new BookingManagementException("End time field must not be empty.");
		}

		// Validate that startTime and endTime are within 24-hour format
		int startTime = Integer.parseInt(userDTO.getStartTime());
		int endTime = Integer.parseInt(userDTO.getEndTime());

		if (startTime < 0 || startTime >= 24 || endTime <= 0 || endTime > 24 || endTime < startTime) {
			throw new BookingManagementException(
					"Invalid start or end time. Please provide valid 24-hour format times.");
		}

		// Validate that the date is today or in the future
		LocalDate today = LocalDate.now();
		LocalDate userDate = LocalDate.parse(userDTO.getDate());

		/*LocalDate today = LocalDate.now();
		LocalDate userDate;
		try {
			userDate = LocalDate.parse(userDTO.getDate());
		} catch (DateTimeParseException e) {
			throw new BookingManagementException("Invalid date format. Please provide a valid date.");
		}*/

		if (userDate.isBefore(today)) {
			throw new BookingManagementException("Invalid date. The date must be today or a future date.");
		}

		User user = dtoToEntity(userDTO);
		List<User> users = userRepository.findAll();

		// Check for time conflicts and collect conflicting users
		List<User> conflictingUsers = users.stream().filter(u -> u.getDate().equals(user.getDate())
				&& ((startTime >= Integer.parseInt(u.getStartTime()) && startTime < Integer.parseInt(u.getEndTime()))
						|| (endTime > Integer.parseInt(u.getStartTime()) && endTime <= Integer.parseInt(u.getEndTime()))
						|| (startTime <= Integer.parseInt(u.getStartTime())
								&& endTime >= Integer.parseInt(u.getEndTime()))))
				.collect(Collectors.toList());
		
		 // Check for time conflicts
        /*List<User> conflictingUsers = users.stream().filter(u -> u.getDate().equals(user.getDate())
            && ((Integer.parseInt(userDTO.getStartTime()) >= Integer.parseInt(u.getStartTime())
                && Integer.parseInt(userDTO.getStartTime()) < Integer.parseInt(userDTO.getEndTime()))
                || (Integer.parseInt(userDTO.getEndTime()) > Integer.parseInt(u.getStartTime())
                && Integer.parseInt(userDTO.getEndTime()) <= Integer.parseInt(u.getEndTime()))
                || (Integer.parseInt(userDTO.getStartTime()) <= Integer.parseInt(userDTO.getStartTime())
                && Integer.parseInt(userDTO.getEndTime()) >= Integer.parseInt(userDTO.getEndTime()))))
            .collect(Collectors.toList());*/


		// Set conflict status for the new user
		boolean isConflict = !conflictingUsers.isEmpty();
		user.setConflictStatus(isConflict ? "Conflict" : "Non-Conflict");

		// Save the user with conflict status
		User savedUser = userRepository.save(user);

		// Update conflict status of conflicting users
		for (User conflictingUser : conflictingUsers) {
			conflictingUser.setConflictStatus("Conflict");
			userRepository.save(conflictingUser);
		}

		// Return the saved user DTO
		return entityToDTO(savedUser);
	}
 
	/*@Override
	public List<UserDTO> getAllUsers() {
		List<User> users = userRepository.findAll();
		return users.stream().map(this::entityToDTO).collect(Collectors.toList());
	}*/
	
	@Override
	public List<UserDTO> getAllUsers() {
	    List<User> users = userRepository.findAll();
	    
	    // Sort the users by date, start time, and end time in ascending order
	    List<User> sortedUsers = users.stream()
	                                  .sorted(Comparator.comparing(User::getDate)
	                                                    .thenComparing(User::getStartTime)
	                                                    .thenComparing(User::getEndTime))
	                                  .collect(Collectors.toList());
	    
	    // Convert the sorted list to DTOs
	    return sortedUsers.stream()
	                      .map(this::entityToDTO)
	                      .collect(Collectors.toList());
	}
	
	/* @Override
	    public List<UserDTO> getAllUsers() {
	        LocalDate today = LocalDate.now();
	        List<User> users = userRepository.findAll();
	        
	        // Filter users with today's date or in the future and sort them
	        List<User> filteredSortedUsers = users.stream()
	                                              .filter(user -> LocalDate.parse(user.getDate()).isEqual(today) || LocalDate.parse(user.getDate()).isAfter(today))
	                                              .sorted(Comparator.comparing(User::getDate)
	                                                                .thenComparing(User::getStartTime)
	                                                                .thenComparing(User::getEndTime))
	                                              .collect(Collectors.toList());
	        
	        // Convert the filtered and sorted list to DTOs
	        return filteredSortedUsers.stream()
	                                  .map(this::entityToDTO)
	                                  .collect(Collectors.toList());
	    }*/


	@Override
	public UserDTO getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		return entityToDTO(user);
	}

	
	 /* @Override public UserDTO updateUser(Long id, UserDTO userDTO) { User
	 * existingUser = userRepository.findById(id).orElseThrow(() -> new
	 * RuntimeException("User not found")); existingUser.setName(userDTO.getName());
	 * existingUser.setDate(userDTO.getDate());
	 * existingUser.setStartTime(userDTO.getStartTime());
	 * existingUser.setEndTime(userDTO.getEndTime());
	 * existingUser.setStatus(userDTO.getStatus());
	 * 
	 * List<User> users = userRepository.findAll();
	 * 
	 * // Check for time conflicts List<User> conflictingUsers = users.stream()
	 * .filter(u -> !u.getId().equals(id) &&
	 * u.getDate().equals(existingUser.getDate()) &&
	 * ((Integer.parseInt(existingUser.getStartTime()) >=
	 * Integer.parseInt(u.getStartTime()) &&
	 * Integer.parseInt(existingUser.getStartTime()) <
	 * Integer.parseInt(u.getEndTime())) ||
	 * (Integer.parseInt(existingUser.getEndTime()) >
	 * Integer.parseInt(u.getStartTime()) &&
	 * Integer.parseInt(existingUser.getEndTime()) <=
	 * Integer.parseInt(u.getEndTime())))) .collect(Collectors.toList());
	 * 
	 * existingUser.setConflictStatus(conflictingUsers.isEmpty() ? "Non-Conflict" :
	 * "Conflict");
	 * 
	 * // Save updated user User updatedUser = userRepository.save(existingUser);
	 * 
	 * // Handle conflict status updates for other users if
	 * (existingUser.getStatus().equals("Approved")) { for (User conflictingUser :
	 * conflictingUsers) { conflictingUser.setConflictStatus("Conflict");
	 * if(conflictingUser.getStatus().equals("Approved")) {
	 * conflictingUser.setStatus("On-Hold"); } userRepository.save(conflictingUser);
	 * } existingUser.setConflictStatus("Non-Conflict");
	 * userRepository.save(existingUser); } else {
	 * existingUser.setConflictStatus(conflictingUsers.isEmpty() ? "Non-Conflict" :
	 * "Conflict");
	 * 
	 * userRepository.save(existingUser); }
	 * 
	 * return entityToDTO(updatedUser); }
	 */

	@Override
	public UserDTO updateUser(Long id, UserDTO userDTO) {
		User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		existingUser.setName(userDTO.getName());
		existingUser.setDate(userDTO.getDate());
		existingUser.setStartTime(userDTO.getStartTime());
		existingUser.setEndTime(userDTO.getEndTime());
		existingUser.setStatus(userDTO.getStatus());

		List<User> users = userRepository.findAll();

		// Check for time conflicts
		int startTime = Integer.parseInt(existingUser.getStartTime());
		int endTime = Integer.parseInt(existingUser.getEndTime());

		List<User> conflictingUsers = users.stream().filter(u -> !u.getId().equals(id)
				&& u.getDate().equals(existingUser.getDate())
				&& ((startTime >= Integer.parseInt(u.getStartTime()) && startTime < Integer.parseInt(u.getEndTime()))
						|| (endTime > Integer.parseInt(u.getStartTime()) && endTime <= Integer.parseInt(u.getEndTime()))
						|| (startTime <= Integer.parseInt(u.getStartTime())
								&& endTime >= Integer.parseInt(u.getEndTime()))))
				.collect(Collectors.toList());

		existingUser.setConflictStatus(conflictingUsers.isEmpty() ? "Non-Conflict" : "Conflict");

		// Save updated user
		User updatedUser = userRepository.save(existingUser);

		// Handle conflict status updates for other users
		if (existingUser.getStatus().equals("Approved")) {
			for (User conflictingUser : conflictingUsers) {
				conflictingUser.setConflictStatus("Conflict");
				if (conflictingUser.getStatus().equals("Approved")) {
					conflictingUser.setStatus("On-Hold");
				}
				userRepository.save(conflictingUser);
			}
			existingUser.setConflictStatus("Non-Conflict");
		} else {
			existingUser.setConflictStatus(conflictingUsers.isEmpty() ? "Non-Conflict" : "Conflict");
		}
		userRepository.save(existingUser);

		return entityToDTO(updatedUser);
	}
	
	/*@Override
	public UserDTO updateUser(Long id, UserDTO userDTO) {
	    User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
	    existingUser.setName(userDTO.getName());
	    existingUser.setDate(userDTO.getDate());
	    existingUser.setStartTime(userDTO.getStartTime());
	    existingUser.setEndTime(userDTO.getEndTime());
	    existingUser.setStatus(userDTO.getStatus());

	    List<User> users = userRepository.findAll();

	    int startTime = Integer.parseInt(existingUser.getStartTime());
	    int endTime = Integer.parseInt(existingUser.getEndTime());

	    List<User> conflictingUsers = users.stream()
	        .filter(u -> !u.getId().equals(id)
	            && u.getDate().equals(existingUser.getDate())
	            && ((startTime >= Integer.parseInt(u.getStartTime()) && startTime < Integer.parseInt(u.getEndTime()))
	                || (endTime > Integer.parseInt(u.getStartTime()) && endTime <= Integer.parseInt(u.getEndTime()))
	                || (startTime <= Integer.parseInt(u.getStartTime()) && endTime >= Integer.parseInt(u.getEndTime()))))
	        .collect(Collectors.toList());

	    boolean isConflict = !conflictingUsers.isEmpty();
	    existingUser.setConflictStatus(isConflict ? "Conflict" : "Non-Conflict");

	    User updatedUser = userRepository.save(existingUser);

	    if (existingUser.getStatus().equals("Approved")) {
	        for (User conflictingUser : conflictingUsers) {
	            conflictingUser.setConflictStatus("Conflict");
	            if (conflictingUser.getStatus().equals("Approved")) {
	                conflictingUser.setStatus("On-Hold");
	            }
	            userRepository.save(conflictingUser);
	        }
	        existingUser.setConflictStatus("Non-Conflict");
	    } else {
	        existingUser.setConflictStatus(isConflict ? "Conflict" : "Non-Conflict");
	    }
	    userRepository.save(existingUser);

	    return entityToDTO(updatedUser);
	}*/


	@Override
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	// convert dto to entity
	private User dtoToEntity(UserDTO userDTO) {
		return modelMapper.map(userDTO, User.class);
	}

	// convert entity to dto
	private UserDTO entityToDTO(User user) {
		return modelMapper.map(user, UserDTO.class);
	}

}
