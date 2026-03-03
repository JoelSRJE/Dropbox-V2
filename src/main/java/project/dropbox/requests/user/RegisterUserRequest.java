package project.dropbox.requests.user;

public record RegisterUserRequest(
        String username,
        String password
) {
}
