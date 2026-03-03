package project.dropbox.services.folder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.dropbox.exceptions.folder.FolderDoesntExistException;
import project.dropbox.exceptions.folder.FolderNameIsEmptyException;
import project.dropbox.exceptions.folder.FolderOwnerIsEmptyException;
import project.dropbox.exceptions.folder.FolderOwnerIsntSameException;
import project.dropbox.models.folder.FolderEntity;
import project.dropbox.models.user.User;
import project.dropbox.repositories.folder.FolderRepository;
import project.dropbox.repositories.user.UserRepository;
import project.dropbox.requests.folder.CreateFolderRequest;
import project.dropbox.requests.folder.UpdateFolderRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FolderService implements IFolderService {

    // Dependencies för servicen
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    /**
     * Hittar en folder baserat på dess id och verifierar att den tillhör användaren.
     *
     * @param folderId - id:et för folder.
     * @param userId - id:et för användaren.
     * @return - ett FolderEntity objekt.
     * @throws FolderDoesntExistException - OM objektet inte existerar eller tillhör användaren.
     */
    @Override
    public FolderEntity getFolderById(UUID folderId, UUID userId) {

        FolderEntity foundFolder = folderRepository.findByFolderIdAndFolderOwner_UserId(folderId, userId);

        if (foundFolder == null) {
            throw new FolderDoesntExistException();
        }

        return foundFolder;
    }

    /**
     * Skapar en folder baserat på informationen i request.
     *
     * @param request - innehåller namn på folder, ägarens användar-id samt id:et för parentFolder OM det ska finnas.
     * @param userId - id:et för användaren.
     * @return - FolderEntity-objektet OM det skapas.
     * @throws FolderNameIsEmptyException - OM foldernamn saknas eller är tomt.
     * @throws FolderOwnerIsEmptyException - OM ägare saknas eller inte existerar.
     * @throws FolderDoesntExistException - OM den angivna parent-foldern inte finns.
     */
    @Override
    public FolderEntity createFolder(CreateFolderRequest request, UUID userId) {

        if (request.folderName() == null || request.folderName().isBlank()) {
            throw new FolderNameIsEmptyException();
        }

        if (userId == null) {
            throw new FolderOwnerIsEmptyException();
        }

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new FolderOwnerIsEmptyException());

        FolderEntity parent = null;

        if (request.parentFolder() != null) {
            parent = folderRepository.findByFolderIdAndFolderOwner_UserId(request.parentFolder(), userId);
            if (parent == null) {
                throw new FolderDoesntExistException();
            }
        }

        FolderEntity newFolder = new FolderEntity(
                request.folderName(),
                owner,
                parent
        );

        return folderRepository.save(newFolder);
    }

    /**
     * Raderar en folder baserat på folderId och folderOwner
     *
     * @param folderId - id:et för foldern som ska raderas.
     * @param userId - id:et för användaren.
     * @return - det raderade FolderEntity-objektet.
     * @throws FolderDoesntExistException - OM foldern inte existerar eller inte tillhör ägaren via ffindByFolderIdAndFolderOwner_UserId-funktionen.
     * @throws FolderOwnerIsntSameException - OM foldern inte tillhör den angivna ägaren
     */
    @Override
    public FolderEntity deleteFolder(UUID folderId, UUID userId) {
        FolderEntity folder = folderRepository.findByFolderIdAndFolderOwner_UserId(folderId, userId);
        if (folder == null) {
            throw new FolderDoesntExistException();
        }

        folderRepository.delete(folder);
        return folder;
    }

    /**
     * Hämtar en lista med folders som en användare äger, OM de finns.
     *
     * @param userId - id:et för ägaren.
     * @return - en lista med FolderEntity-objekt om de finns.
     */
    @Override
    public List<FolderEntity> getAllFoldersWithFilesByUser(UUID userId) {
        return folderRepository.findAllByFolderOwner_UserId(userId);
    }

    /**
     * Uppdaterar en folder via information från request. Endast namnet kan uppdateras.
     *
     * @param folderId - id:et för vilken folder som ska uppdateras.
     * @param request - den nya datan/informationen som ska uppdatera ett FileEntity-objekt i databasen.
     * @param userId - id:et för användaren.
     * @return - det uppdaterade FolderEntity-objektet med ett nytt namn.
     * @throws FolderDoesntExistException - OM FolderEntity-objektet inte existerar.
     */
    @Override
    public FolderEntity updateFolderName(UUID folderId, UpdateFolderRequest request, UUID userId) {
        FolderEntity folder = folderRepository.findByFolderIdAndFolderOwner_UserId(folderId, userId);
        if (folder == null) {
            throw new FolderDoesntExistException();
        }

        if (request.folderName() != null && !request.folderName().isBlank()) {
            folder.setFolderName(request.folderName());
        }

        return folderRepository.save(folder);
    }
}
