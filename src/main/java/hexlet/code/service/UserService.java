package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.map(user);
    }

    public UserDTO create(UserCreateDTO dto) {
        User user = userMapper.map(dto);
        user.setPasswordDigest(passwordEncoder.encode(dto.getPassword().get()));
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO update(Long id, UserCreateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.update(dto, user);

        if (dto.getPassword().isPresent()) {
            user.setPasswordDigest(passwordEncoder.encode(dto.getPassword().get()));
        }

        userRepository.save(user);
        return userMapper.map(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
