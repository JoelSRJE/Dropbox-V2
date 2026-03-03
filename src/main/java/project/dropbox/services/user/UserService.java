package project.dropbox.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.dropbox.dto.user.GetUserDto;
import project.dropbox.dto.user.UpdatedUserDto;
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
     * Hittar en användare baserat på dess Github Id och validerar input, för oauth2
     *
     * @param githubId - GithubId:et för användaren.
     * @return - User-objektet OM det finns.
     * @throws GithubIdIsNullException - OM githubid:et är null/blank.
     * @throws UserDoesntExistsException - OM användaren inte finns.
     */
    @Override
    public User findUserByGithubId(String githubId) {

        if (githubId == null || githubId.isBlank()) {
            throw new GithubIdIsNullException();
        }

        return userRepository.findByGithubId(githubId)
                .orElseThrow(UserDoesntExistsException::new);
    }

    /**
     * Hittar en användare baserat på dess userId och validerar input.
     *
     * @param userId - Id:et för användaren.
     * @return - User-objektet OM det finns.
     * @throws UserIdIsNullException - OM userId är null.
     * @throws UserDoesntExistsException - OM användaren inte finns i databasen.
     */
    @Override
    public User findUserById(UUID userId) {

        if (userId == null) {
            throw new UserIdIsNullException();
        }

        return userRepository.findById(userId)
                .orElseThrow(UserDoesntExistsException::new);
    }

    /**
     * Uppdaterar en användares username och endast username, samt validerar att requesten inte är tom.
     *
     * @param userId - Id:et för användaren.
     * @param request - UpdateUserRequesten med innehållet "username".
     * @return - User-objektet som SKA vara uppdaterat.
     * @throws UserIdIsNullException - Från findUserById.
     * @throws UserDoesntExistsException - Från findUserById.
     */
    @Override
    public UpdatedUserDto updateUser(UUID userId, UpdateUserRequest request) {
        User user = findUserById(userId);

        if (request.username() != null && !request.username().isBlank()) {
            user.setUsername(request.username());
        }
        userRepository.save(user);

        return UpdatedUserDto.from(user);
    }

    /**
     * Raderar en användare OM den finns i databasen.
     *
     * @param userId - Id:et för användaren.
     * @return - Det raderade user-objektet.
     * @throws UserIdIsNullException - Från findUserById.
     * @throws UserDoesntExistsException - Från findUserById.
     */
    @Override
    public User deleteUser(UUID userId) {
        User deletedUser = findUserById(userId);

        userRepository.delete(deletedUser);

        return deletedUser;
    }

    /**
     * Hämtar alla registrerade användare från databasen.
     *
     * @return - En lista av användare utifrån GetUserDto:n.
     */
    @Override
    public List<GetUserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(GetUserDto::from)
                .toList();
    }
}
