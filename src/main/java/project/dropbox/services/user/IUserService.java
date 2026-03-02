package project.dropbox.services.user;

import project.dropbox.dto.user.GetUserDto;
import project.dropbox.models.user.User;
import project.dropbox.requests.user.UpdateUserRequest;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    // Hitta användare via githubId.
    User findUserByGithubId(String githubId);

    // Hitta användare via UUID/userId.
    User findUserById(UUID userId);

    // Authorized methods

    // Uppdaterar en användare. Går bara att uppdatera email i nuläget.
    User updateUser(UUID userId, UpdateUserRequest request);

    // Raderar en användare baserat på dess id.
    User deleteUser(UUID userId);

    // Hämtar alla användare som finns i databasen.
    List<GetUserDto> getAllUsers();
}
