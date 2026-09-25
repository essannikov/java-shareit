package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByEmailIgnoreCase(String emailSearch);
}