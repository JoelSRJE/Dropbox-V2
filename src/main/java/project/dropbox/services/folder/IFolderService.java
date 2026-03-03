package project.dropbox.services.folder;

import project.dropbox.models.folder.FolderEntity;
import project.dropbox.requests.folder.CreateFolderRequest;
import project.dropbox.requests.folder.UpdateFolderRequest;

import java.util.List;
import java.util.UUID;

// Kräver Authorization
public interface IFolderService {
    // Hittar en specific folder baserat på dess id och ägarens id.
    FolderEntity getFolderById(UUID folderId, UUID folderOwner);

    // Skapar ett nytt FolderEntity objekt som sedan sparas i databasen.
    FolderEntity createFolder(CreateFolderRequest request, UUID userId);

    // Raderar en folder baserat på dess id samt ägarens id.
    FolderEntity deleteFolder(UUID folderId, UUID folderOwner);

    // Hittar ALLA folders för en användare med filer.
    List<FolderEntity> getAllFoldersWithFilesByUser(UUID ownerId);

    // Låter användaren uppdate en folders namn, och endast namnet.
    FolderEntity updateFolderName(UUID folderId, UpdateFolderRequest request, UUID userId);
}
