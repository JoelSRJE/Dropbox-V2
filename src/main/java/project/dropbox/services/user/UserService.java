package project.dropbox.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.dropbox.dto.user.GetUserDto;
import project.dropbox.exceptions.user.*;
import project.dropbox.models.user.User;
import project.dropbox.repositories.user.UserRepository;
import project.dropbox.requests.user.UpdateUserRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    // Dependencies för servicen
    private final UserRepository userRepository;


    /**
     *
     *
     * @param githubId
     * @return
     */
    @Override
    public User findUserByGithubId(String githubId) {

        if (githubId == null || githubId.isBlank()) {
            throw new GithubIdIsNullException();
        }

        return userRepository.findByGithubId(githubId)
                .orElseThrow(UserDoesntExistsException::new);
    }

    @Override
    public User findUserById(UUID userId) {

        if (userId == null) {
            throw new UserIdIsNullException();
        }

        return userRepository.findById(userId)
                .orElseThrow(UserDoesntExistsException::new);
    }

    @Override
    public User updateUser(UUID userId, UpdateUserRequest request) {
        User user = findUserById(userId);

        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }

        return userRepository.save(user);
    }

    @Override
    public User deleteUser(UUID userId) {
        User deletedUser = findUserById(userId);

        userRepository.delete(deletedUser);

        return deletedUser;
    }

    @Override
    public List<GetUserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(GetUserDto::from)
                .toList();
    }
}
