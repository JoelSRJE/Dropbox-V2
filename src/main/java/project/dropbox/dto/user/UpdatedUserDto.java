package project.dropbox.dto.user;

import project.dropbox.models.user.User;

public record UpdatedUserDto(
        String username
) {
    public static UpdatedUserDto from(User user) {
        return new UpdatedUserDto(user.getUsername());
    }
}
