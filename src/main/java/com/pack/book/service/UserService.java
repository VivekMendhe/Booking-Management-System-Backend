package com.pack.book.service;

import java.util.List;

import com.pack.book.dto.UserDTO;

public interface UserService {

	UserDTO addUser(UserDTO userDTO);

	List<UserDTO>  getAllUsers();

	UserDTO getUserById(Long id);

	UserDTO updateUser(Long id, UserDTO userDTO);

	void deleteUser(Long id);
}
