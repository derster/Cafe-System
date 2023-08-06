package com.derster.cafesystem.dao;

import com.derster.cafesystem.pojo.User;
import com.derster.cafesystem.wrapper.UserWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    User findByEmailId(@Param("email") String email);

    List<UserWrapper> getAllUsers();

    @Transactional
    @Modifying
    void updateStatus(@Param("status") boolean status, @Param("id") Integer id);

    List<String> getAllAdmins();

    User findByEmail(String email);
}
