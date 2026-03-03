package project.dropbox.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import project.dropbox.models.file.FileEntity;
import project.dropbox.models.folder.FolderEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(unique = true)
    private String username;

    @Column(unique = true, nullable = false)
    private String githubId;

    @Column
    @JsonIgnore
    private String passwordHash;

    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "folderOwner",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<FolderEntity> folders;

    @OneToMany(
            mappedBy = "fileOwner",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<FileEntity> files;

    protected User() {}

    public User(String username, String githubId) {
        this.username = username;
        this.githubId = githubId;
        this.createdAt = LocalDateTime.now();
        this.accountType = AccountType.USER;
    }
}
