package project.dropbox.exceptions.user;

public class GithubIdIsNullException extends RuntimeException {
    public GithubIdIsNullException() {
        super("GithubId cannot be null or blank!");
    }
}
