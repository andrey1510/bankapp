package com.accountservice.services;


import com.accountservice.dto.UserDTO;
import com.accountservice.entities.User;
import com.accountservice.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createAccount(UserDTO dto) {
        User user = new User();
        return userRepository.save(user);
    }

    @Override
    public User updateAccount(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);
        return userRepository.save(user);
    }

}
