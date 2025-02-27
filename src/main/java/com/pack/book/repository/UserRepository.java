package com.pack.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pack.book.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
