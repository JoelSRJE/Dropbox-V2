package project.dropbox.controllers.folder;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.dropbox.dto.folder.DeletedFolderDto;
import project.dropbox.dto.folder.GetFolderDto;
import project.dropbox.dto.folder.NewFolderDto;
import project.dropbox.dto.folder.UpdateFolderDto;
import project.dropbox.models.folder.FolderEntity;
import project.dropbox.models.user.User;
import project.dropbox.requests.folder.CreateFolderRequest;
import project.dropbox.requests.folder.UpdateFolderRequest;
import project.dropbox.services.folder.FolderService;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<EntityModel<NewFolderDto>> createFolder(
            @RequestBody CreateFolderRequest request,
            @AuthenticationPrincipal User authenticatedUser
            ) {
        FolderEntity folder = folderService.createFolder(request, authenticatedUser.getUserId());

        NewFolderDto newFolderDto = NewFolderDto.from(folder);

        EntityModel<NewFolderDto> folderModel = EntityModel.of(newFolderDto);

        //
        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .getFolderById(folder.getFolderId(), null))
                        .withSelfRel()
        ):


        return ResponseEntity.created(EntityModel<newFolderDto>);
    }

    @GetMapping
    public ResponseEntity<List<GetFolderDto>> getAllFoldersForUser(
            @AuthenticationPrincipal User authenticatedUser
            ) {
        return ResponseEntity.ok(folderService.getAllFoldersWithFilesByUser(authenticatedUser.getUserId())
                .stream()
                .map(GetFolderDto::from)
                .toList());
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<EntityModel<GetFolderDto>> getFolderById(
            @PathVariable UUID folderId,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        FolderEntity folder = folderService.get
    }

    @PutMapping("/{folderId}")
    public ResponseEntity<UpdateFolderDto> updateFolder(
            @PathVariable UUID folderId,
            @RequestBody UpdateFolderRequest request,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        FolderEntity updatedFolder = folderService.updateFolderName(folderId, request, authenticatedUser.getUserId());

        return ResponseEntity.ok(UpdateFolderDto.from(updatedFolder));
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<DeletedFolderDto> deleteFolder(
            @PathVariable UUID folderId,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        FolderEntity deletedFolder = folderService.deleteFolder(folderId, authenticatedUser.getUserId());

        return ResponseEntity.ok(DeletedFolderDto.from(deletedFolder));
    }
}
