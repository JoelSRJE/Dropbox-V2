package project.dropbox.requests.user;

public record LoginUserRequest(
        String username,
        String password
) {
}
