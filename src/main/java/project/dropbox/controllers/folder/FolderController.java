package project.dropbox.controllers.folder;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
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

// Kräver Authorization för att få åtkomst
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

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .getFolderById(folder.getFolderId(), authenticatedUser))
                        .withSelfRel()
        );

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .updateFolder(folder.getFolderId(), null, authenticatedUser))
                        .withRel("update")
        );

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .deleteFolder(folder.getFolderId(), authenticatedUser))
                        .withRel("delete")
        );

        return ResponseEntity
                .created(linkTo(methodOn(FolderController.class)
                        .getFolderById(folder.getFolderId(), authenticatedUser))
                        .toUri()
                )
                .body(folderModel);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<GetFolderDto>>> getAllFoldersForUser(
            @AuthenticationPrincipal User authenticatedUser
    ) {

        List<EntityModel<GetFolderDto>> folders = folderService
                .getAllFoldersWithFilesByUser(authenticatedUser.getUserId())
                .stream()
                .map(folder -> {
                    GetFolderDto dto = GetFolderDto.from(folder);

                    EntityModel<GetFolderDto> model = EntityModel.of(dto);

                    model.add(
                            linkTo(methodOn(FolderController.class)
                                    .getFolderById(folder.getFolderId(), authenticatedUser))
                                    .withSelfRel()
                    );

                    return model;
                })
                .toList();

        CollectionModel<EntityModel<GetFolderDto>> collectionModel =
                CollectionModel.of(folders);

        collectionModel.add(
                linkTo(methodOn(FolderController.class)
                        .getAllFoldersForUser(authenticatedUser))
                        .withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<EntityModel<GetFolderDto>> getFolderById(
            @PathVariable UUID folderId,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        FolderEntity folder = folderService.getFolderById(folderId, authenticatedUser.getUserId());

        GetFolderDto getFolderDto = GetFolderDto.from(folder);

        EntityModel<GetFolderDto> folderModel = EntityModel.of(getFolderDto);

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .getFolderById(folderId, authenticatedUser))
                        .withSelfRel()
        );

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .updateFolder(folderId, null ,authenticatedUser))
                        .withRel("update")
        );

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .deleteFolder(folderId, authenticatedUser))
                        .withRel("delete")
        );

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .getAllFoldersForUser(authenticatedUser))
                        .withRel("folders")
        );

        return ResponseEntity.ok(folderModel);
    }

    @PutMapping("/{folderId}")
    public ResponseEntity<EntityModel<UpdateFolderDto>> updateFolder(
            @PathVariable UUID folderId,
            @RequestBody UpdateFolderRequest request,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        FolderEntity updatedFolder = folderService.updateFolderName(folderId, request, authenticatedUser.getUserId());

        UpdateFolderDto updatedFolderDto = UpdateFolderDto.from(updatedFolder);

        EntityModel<UpdateFolderDto> folderModel = EntityModel.of(updatedFolderDto);

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .getFolderById(folderId, authenticatedUser))
                        .withSelfRel()
        );

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .updateFolder(folderId, null, authenticatedUser))
                        .withRel("update")
        );

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .deleteFolder(folderId, authenticatedUser))
                        .withRel("delete")
        );

        folderModel.add(
                linkTo(methodOn(FolderController.class)
                        .getAllFoldersForUser(authenticatedUser))
                        .withRel("folders")
        );

        return ResponseEntity.ok(folderModel);

    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<DeletedFolderDto> deleteFolder(
            @PathVariable UUID folderId,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        folderService.deleteFolder(folderId, authenticatedUser.getUserId());

        return ResponseEntity.noContent().build();
    }
}
