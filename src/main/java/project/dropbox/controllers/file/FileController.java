package project.dropbox.controllers.file;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.dropbox.dto.file.CreateFileDto;
import project.dropbox.dto.file.DeletedFileDto;
import project.dropbox.dto.file.GetFileDto;
import project.dropbox.dto.file.UpdatedFileDto;
import project.dropbox.models.file.FileEntity;
import project.dropbox.models.folder.FolderEntity;
import project.dropbox.models.user.User;
import project.dropbox.requests.file.*;
import project.dropbox.services.file.FileService;
import project.dropbox.services.folder.FolderService;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// Kräver Authorization för att få åtkomst
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<EntityModel<CreateFileDto>> createFile(
            @RequestBody CreateFileRequest request,
            @AuthenticationPrincipal User authenticatedUser
    ) {

        FileEntity file = fileService.createFile(request, authenticatedUser.getUserId());

        CreateFileDto dto = CreateFileDto.from(file);
        EntityModel<CreateFileDto> model = EntityModel.of(dto);

        model.add(linkTo(methodOn(FileController.class)
                .getSpecificFile(file.getFileId(), authenticatedUser))
                .withSelfRel());

        model.add(linkTo(methodOn(FileController.class)
                .getFiles(authenticatedUser))
                .withRel("files"));

        model.add(linkTo(methodOn(FileController.class)
                .downloadFile(file.getFileId(), authenticatedUser))
                .withRel("download"));

        model.add(linkTo(methodOn(FileController.class)
                .updateFile(file.getFileId(), null, authenticatedUser))
                .withRel("update"));

        model.add(linkTo(methodOn(FileController.class)
                .deleteFile(file.getFileId(), authenticatedUser))
                .withRel("delete"));

        if (file.getFolder() != null) {
            model.add(linkTo(methodOn(FileController.class)
                    .getFilesByfolder(file.getFolder().getFolderId(), authenticatedUser))
                    .withRel("folder"));
        }

        return ResponseEntity
                .created(linkTo(methodOn(FileController.class)
                        .getSpecificFile(file.getFileId(), authenticatedUser))
                        .toUri())
                .body(model);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<EntityModel<GetFileDto>> getSpecificFile(
            @PathVariable UUID fileId,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        FileEntity file = fileService.getFileByIdAndUser(fileId, authenticatedUser.getUserId());

        GetFileDto getFileDto = GetFileDto.from(file);

        EntityModel<GetFileDto> fileModel = EntityModel.of(getFileDto);

        fileModel.add(linkTo(methodOn(FileController.class)
                .getSpecificFile(fileId, authenticatedUser))
                .withSelfRel());

        fileModel.add(linkTo(methodOn(FileController.class)
                .getFiles(authenticatedUser))
                .withRel("files"));

        fileModel.add(linkTo(methodOn(FileController.class)
                .downloadFile(fileId, authenticatedUser))
                .withRel("download"));

        fileModel.add(linkTo(methodOn(FileController.class)
                .updateFile(fileId, null, authenticatedUser))
                .withRel("update"));

        fileModel.add(linkTo(methodOn(FileController.class)
                .deleteFile(fileId, authenticatedUser))
                .withRel("delete"));

        if (file.getFolder() != null) {
            fileModel.add(
                    linkTo(methodOn(FileController.class)
                            .getFilesByfolder(file.getFolder().getFolderId(), authenticatedUser))
                            .withRel("folder")
            );
        }

        return ResponseEntity.ok(fileModel);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<GetFileDto>>> getFiles(
            @AuthenticationPrincipal User authenticatedUser
    ) {
        List<EntityModel<GetFileDto>> fileModels = fileService.findFilesByOwner(authenticatedUser.getUserId())
                        .stream()
                                .map(file -> {
                                    GetFileDto getFileDto = GetFileDto.from(file);

                                    EntityModel<GetFileDto> fileModel = EntityModel.of(getFileDto);

                                    fileModel.add(
                                            linkTo(methodOn(FileController.class)
                                                    .getSpecificFile(file.getFileId(), authenticatedUser))
                                                    .withSelfRel()
                                    );

                                    return fileModel;
                                })
                                        .toList();

        CollectionModel<EntityModel<GetFileDto>> collectionModel = CollectionModel.of(fileModels);

        collectionModel.add(
                linkTo(methodOn(FileController.class)
                        .getFiles(authenticatedUser))
                        .withSelfRel()
        );

        collectionModel.add(
                linkTo(methodOn(FileController.class)
                        .createFile(null, authenticatedUser))
                        .withRel("create")
        );


       return ResponseEntity.ok(collectionModel);
    }

    // Rör inte, denna stämmer.
    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable UUID fileId,
            @AuthenticationPrincipal User authenticatedUser
    ) {

        FileEntity file = fileService.getFileByIdAndUser(fileId, authenticatedUser.getUserId());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getData());
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<EntityModel<DeletedFileDto>> deleteFile(
            @PathVariable UUID fileId,
            @AuthenticationPrincipal User authenticatedUser
            ) {
        fileService.deleteFile(fileId, authenticatedUser.getUserId());

        return ResponseEntity.noContent().build();

    }

    @GetMapping("/folders/{folderId}/files")
    public ResponseEntity<CollectionModel<EntityModel<GetFileDto>>> getFilesByfolder(
            @PathVariable UUID folderId,
            @AuthenticationPrincipal User authenticatedUser
            ) {

        FolderEntity folder = folderService.getFolderById(folderId, authenticatedUser.getUserId());

        List<EntityModel<GetFileDto>> fileModels = fileService.findFilesByFolder(folderId, authenticatedUser.getUserId())
                .stream()
                .map(file -> {

                    GetFileDto getFileDto = GetFileDto.from(file);

                    EntityModel<GetFileDto> fileModel = EntityModel.of(getFileDto);

                    fileModel.add(
                            linkTo(methodOn(FileController.class)
                                    .getSpecificFile(file.getFileId(), authenticatedUser))
                                    .withSelfRel()
                    );

                    return fileModel;
                })
                .toList();

        CollectionModel<EntityModel<GetFileDto>> collectionModel = CollectionModel.of(fileModels);

        collectionModel.add(
                linkTo(methodOn(FileController.class)
                        .getFilesByfolder(folderId, authenticatedUser))
                        .withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<EntityModel<UpdatedFileDto>> updateFile(
            @PathVariable UUID fileId,
            @RequestBody UpdateFileRequest request,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        FileEntity file = fileService.updateFileName(fileId, request, authenticatedUser.getUserId());

        UpdatedFileDto updatedFileDto = UpdatedFileDto.from(file);

        EntityModel<UpdatedFileDto> fileModel = EntityModel.of(updatedFileDto);

        fileModel.add(linkTo(methodOn(FileController.class)
                .getSpecificFile(fileId, authenticatedUser))
                .withSelfRel());

        fileModel.add(linkTo(methodOn(FileController.class)
                .getFiles(authenticatedUser))
                .withRel("files"));

        fileModel.add(linkTo(methodOn(FileController.class)
                .downloadFile(fileId, authenticatedUser))
                .withRel("download"));

        fileModel.add(linkTo(methodOn(FileController.class)
                .deleteFile(fileId, authenticatedUser))
                .withRel("delete"));

        if (file.getFolder() != null) {
            fileModel.add(
                    linkTo(methodOn(FileController.class)
                            .getFilesByfolder(file.getFolder().getFolderId(), authenticatedUser))
                            .withRel("folder")
            );
        }

        return ResponseEntity.ok(fileModel);
    }

}
