package project.dropbox.dto.user;

import project.dropbox.models.user.AccountType;
import project.dropbox.models.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeletedUserDto(
        UUID userId,
        String username,
        AccountType accountType,
        LocalDateTime createdAt
) {
    public static DeletedUserDto from(User user) {
        return new DeletedUserDto(
                user.getUserId(),
                user.getUsername(),
                user.getAccountType(),
                user.getCreatedAt()
        );
    }
}
