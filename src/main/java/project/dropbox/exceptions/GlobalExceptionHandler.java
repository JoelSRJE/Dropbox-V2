package project.dropbox.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import project.dropbox.exceptions.file.*;
import project.dropbox.exceptions.folder.*;
import project.dropbox.exceptions.user.*;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // User Exceptions
    @ExceptionHandler(UserDoesntExistsException.class)
    public ResponseEntity<?> handleUserNotFound(UserDoesntExistsException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExistException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(GithubIdIsNullException.class)
    public ResponseEntity<?> handleEmailIsEmpty(GithubIdIsNullException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(UserIdIsNullException.class)
    public ResponseEntity<?> handlePasswordIsEmpty(UserIdIsNullException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    // Folder Exceptions
    @ExceptionHandler(FolderDoesntExistException.class)
    public ResponseEntity<?> handleFolderDoesntExist(FolderDoesntExistException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FolderIdIsNullException.class)
    public ResponseEntity<?> handleFolderIdIsNull(FolderIdIsNullException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FolderNameIsEmptyException.class)
    public ResponseEntity<?> handleFolderNameIsEmpty(FolderNameIsEmptyException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FolderOwnerIsEmptyException.class)
    public ResponseEntity<?> handleFolderOwnerIsEmpty(FolderOwnerIsEmptyException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FolderOwnerIsntSameException.class)
    public ResponseEntity<?> handleFolderOwnerIsntSame(FolderOwnerIsntSameException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", exception.getMessage()));
    }

    // File Exceptions
    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<?> handleFileAlreadyExists(FileAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FileDataIsNullException.class)
    public ResponseEntity<?> handleFileDataIsNull(FileDataIsNullException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FileDoesntExistException.class)
    public ResponseEntity<?> handleFileDoesntExist(FileDoesntExistException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FileFolderIsNullException.class)
    public ResponseEntity<?> handleFileFolderIsNull(FileFolderIsNullException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FileIdIsNullException.class)
    public ResponseEntity<?> handleFileIdIsNull(FileIdIsNullException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FileNameIsEmptyException.class)
    public ResponseEntity<?> handleFileNameIsEmpty(FileNameIsEmptyException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FileOwnerIdIsNullException.class)
    public ResponseEntity<?> handleFileOwnerIdIsNull(FileOwnerIdIsNullException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(FileOwnerIsNullException.class)
    public ResponseEntity<?> handleFileOwnerIsNull(FileOwnerIsNullException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

}
