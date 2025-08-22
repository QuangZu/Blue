package com.techtack.blue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techtack.blue.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
     public Optional<User> findByEmail(String email);
     public Optional<User> findByUsername(String username);
     
     @Query("SELECT u FROM User u WHERE u.phone_number = :phoneNumber")
     public Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
     
     public Optional<User> findByPassword(String password);
     public Optional<User> findByAddress(String address);

    @Query("SELECT DISTINCT u FROM User u WHERE u.username LIKE %:query% OR u.email LIKE %:query%")
    public List<User> searchUser(@Param("query") String query);
}