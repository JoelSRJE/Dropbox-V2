package project.dropbox.services.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.dropbox.exceptions.file.*;
import project.dropbox.exceptions.folder.FolderIdIsNullException;
import project.dropbox.models.file.FileEntity;
import project.dropbox.models.folder.FolderEntity;
import project.dropbox.models.user.User;
import project.dropbox.repositories.file.FileRepository;
import project.dropbox.repositories.folder.FolderRepository;
import project.dropbox.repositories.user.UserRepository;
import project.dropbox.requests.file.CreateFileRequest;
import project.dropbox.requests.file.UpdateFileRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService {

    // Dependency för servicen
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    /**
     * Hittar ett FileEntity-objekt baserat på fileOwner.
     *
     * @param ownerId - id:et för ägaren av FileEntity-objektet.
     * @return - ett FileEntity-objekt om det hittas.
     * @throws FileOwnerIsNullException - OM ägarens id är null.
     * @throws FileDoesntExistException - OM FileEntity-objektet inte existerar.
     */
    @Override
    public List<FileEntity> findFilesByOwner(UUID ownerId) {

        if (ownerId == null) {
            throw new FileOwnerIsNullException();
        }

        return fileRepository.findByFileOwner_UserId(ownerId);
    }

    /**
     * Sparar ett FileEntity-objekt i databasen om allt stämmer.
     *
     * @param request - innehåller all information som ska sparas om allt stämmer.
     * @param userId - id:et för användaren.
     * @return - Det sparade FileEntity-objektet som sparas.
     * @throws FileNameIsEmptyException - OM fileName är null.
     * @throws FileDataIsNullException - OM data är null.
     * @throws FileOwnerIsNullException - OM fileOwner är null.
     * @throws FileFolderIsNullException - OM fileFolder är null.
     * @throws FileAlreadyExistsException - OM FileEntity-objektet redan existerar i databasen.
     */
    @Override
    public FileEntity createFile(CreateFileRequest request, UUID userId) {

        if (request.fileName() == null || request.fileName().isBlank()) {
            throw new FileNameIsEmptyException();
        }

        if (request.data() == null) {
            throw new FileDataIsNullException();
        }

        if (userId == null) {
            throw new FileOwnerIsNullException();
        }

        User owner = userRepository.findById(userId)
                .orElseThrow(FileOwnerIsNullException::new);

        if (request.folder() == null || request.folder().isBlank()) {
            throw new FileFolderIsNullException();
        }

        FolderEntity folder = folderRepository.findById(UUID.fromString(request.folder()))
                .orElseThrow(FileFolderIsNullException::new);

        FileEntity newFile = new FileEntity(
                request.fileName(),
                request.contentType(),
                request.data(),
                folder,
                owner
        );

        boolean exists = fileRepository.existsByFileNameAndFolderAndFileOwner(
                newFile.getFileName(),
                folder,
                owner
        );

        if (exists) {
            throw new FileAlreadyExistsException();
        }

        return fileRepository.save(newFile);
    }

    /**
     * Raderar ett FileEntity-objekt från databasen.
     *
     * @param fileId - id:et för ett FileEntity-objekt.
     * @param ownerId - id:et för en ägare av ett FileEntity-objekt.
     * @return - det raderade FileEntity-objektet.
     * @throws FileIdIsNullException - OM fileId är null.
     * @throws FileOwnerIdIsNullException - OM ownerId är null.
     * @throws FileDoesntExistException - OM FileEntity-objektet inte existerar i databasen.
     */
    @Override
    public FileEntity deleteFile(UUID fileId, UUID ownerId) {

        if (fileId == null) {
            throw new FileIdIsNullException();
        }

        if (ownerId == null) {
            throw new FileOwnerIdIsNullException();
        }

        FileEntity deletedFile = fileRepository.findByFileIdAndFileOwner_UserId(fileId, ownerId)
                .orElseThrow(() -> new FileDoesntExistException());

        fileRepository.delete(deletedFile);

        return deletedFile;
    }

    /**
     * Hittar alla filer för en folder.
     *
     * @param folderId - id:et för foldern.
     * @param ownerId - id:et för ägaren av foldern.
     * @return - en lista med FileEntity-objekt.
     * @throws FolderIdIsNullException - OM folderId är null.
     * @throws FileOwnerIdIsNullException - OM fileOwner är null.
     */
    @Override
    public List<FileEntity> findFilesByFolder(UUID folderId, UUID ownerId) {

        if (folderId == null) {
            throw new FolderIdIsNullException();
        }

        if (ownerId == null) {
            throw new FileOwnerIdIsNullException();
        }

        return fileRepository.findByFolder_FolderIdAndFileOwner_UserId(folderId, ownerId);
    }

    /**
     * Hittar en fil baserat på dess id och användarens.
     *
     * @param fileId - id:et för filen.
     * @param userId - id:et för användaren.
     * @return - FileEntity-objektet.
     * @throws FileIdIsNullException - OM fileId är null.
     * @throws FileOwnerIdIsNullException - OM userId är null.
     * @throws FileDoesntExistException - OM filen inte finns baserat på fileId och userId.
     */
    @Override
    public FileEntity getFileByIdAndUser(UUID fileId, UUID userId) {

        if(fileId == null) {
            throw new FileIdIsNullException();
        }
        if (userId == null) {
            throw new FileOwnerIdIsNullException();
        }

        return fileRepository
                .findByFileIdAndFileOwner_UserId(fileId, userId)
                .orElseThrow(() -> new FileDoesntExistException());
    }

    /**
     * Uppdaterar ett FileEntity-objekt.
     *
     * @param fileId - id:et för ett FileEntity-objekt.
     * @param request - innehåller fileName.
     * @param userId - id:et för användaren.
     * @return - Det uppdaterade FileEntity-objektet.
     * @throws FileDoesntExistException - OM filen inte existerar i databasen.
     * @throws FileIdIsNullException - OM fileId är null.
     * @throws FileOwnerIdIsNullException - OM fileOwner är null.
     */
    @Override
    public FileEntity updateFileName(UUID fileId, UpdateFileRequest request, UUID userId) {

        if (fileId == null) {
            throw new FileIdIsNullException();
        }

        if (userId == null) {
            throw new FileOwnerIdIsNullException();
        }

        FileEntity theFile = fileRepository
                .findByFileIdAndFileOwner_UserId(fileId, userId)
                .orElseThrow(FileDoesntExistException::new);

        if (request.fileName() != null && !request.fileName().isBlank()) {
            theFile.setFileName(request.fileName());
        }

        return fileRepository.save(theFile);
    }
}
