package project.dropbox.controllers.user;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.dropbox.dto.user.DeletedUserDto;
import project.dropbox.dto.user.GetUserDto;
import project.dropbox.dto.user.UpdatedUserDto;
import project.dropbox.models.user.User;
import project.dropbox.requests.user.UpdateUserRequest;
import project.dropbox.services.user.UserService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Test endpoint innan jag implementerar HATEOAS/RESTful.
    @GetMapping("/me")
    public ResponseEntity<EntityModel<GetUserDto>> getCurrentUser(
            @AuthenticationPrincipal User authenticatedUser
    ) {
        GetUserDto getUserDto = GetUserDto.from(
                userService.findUserById(authenticatedUser.getUserId())
        );

        EntityModel<GetUserDto> userModel = EntityModel.of(getUserDto);

        userModel.add(
                linkTo(methodOn(UserController.class)
                        .getCurrentUser(null))
                        .withSelfRel()
        );

        userModel.add(
                linkTo(methodOn(UserController.class)
                        .updateUser(null, null))
                        .withRel("update")
        );

        userModel.add(
                linkTo(methodOn(UserController.class)
                        .deleteUser(null))
                        .withRel("delete")
        );

        return ResponseEntity.ok(userModel);
    }

   @PutMapping
    public ResponseEntity<EntityModel<UpdatedUserDto>> updateUser(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody UpdateUserRequest request
   ) {
       UpdatedUserDto updatedUserDto = userService.updateUser(
               authenticatedUser.getUserId(), request
       );

       EntityModel<UpdatedUserDto> model = EntityModel.of(updatedUserDto);

       model.add(linkTo(methodOn(UserController.class)
               .getCurrentUser(null))
               .withSelfRel());

       model.add(linkTo(methodOn(UserController.class)
               .deleteUser(null))
               .withRel("delete"));


       return ResponseEntity.ok(model);
   }

   // Ska låsa denna så icke admin användare inte kan använda denna!
   @GetMapping
   public ResponseEntity<CollectionModel<EntityModel<GetUserDto>>> getUsers() {

       List<EntityModel<GetUserDto>> users = userService.getAllUsers()
               .stream()
               .map(dto -> {
                   EntityModel<GetUserDto> model = EntityModel.of(dto);

                   model.add(
                           linkTo(methodOn(UserController.class)
                                   .getCurrentUser(null))
                                   .withRel("self")
                   );

                   return model;
               })
               .toList();

       return ResponseEntity.ok(CollectionModel.of(users));
   }

   @DeleteMapping
    public ResponseEntity<DeletedUserDto> deleteUser(
            @AuthenticationPrincipal User authenticatedUser
   ) {
       User deletedUser = userService.deleteUser(authenticatedUser.getUserId());

       return ResponseEntity.ok(DeletedUserDto.from(deletedUser));
   }
}
