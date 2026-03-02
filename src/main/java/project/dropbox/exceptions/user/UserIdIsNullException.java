package project.dropbox.exceptions.user;

public class UserIdIsNullException extends RuntimeException {
    public UserIdIsNullException() {
        super("UserId cannot be null or empty!");
    }
}
