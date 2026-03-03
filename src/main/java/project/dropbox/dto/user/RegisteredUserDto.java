package project.dropbox.dto.user;

import project.dropbox.models.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisteredUserDto(
        UUID userId,
        String username,
        LocalDateTime createdAt
) {
    public static RegisteredUserDto from(User user) {
        return new RegisteredUserDto(user.getUserId(), user.getUsername(), user.getCreatedAt());
    }
}
