package com.example.social_media_app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.example.social_media_app.util.TimeUtil;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 280)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({ "posts", "comments", "likes", "password" })
    private User user;

    @Column(name = "author_id", insertable = false, updatable = false)
    private Long authorId;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("post")
    @ToString.Exclude
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("post")
    @ToString.Exclude
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("uploadOrder ASC")
    @JsonIgnoreProperties("post")
    @ToString.Exclude
    @Builder.Default
    private List<PostMedia> mediaFiles = new ArrayList<>();

    // Location fields
    @Column(name = "location_name")
    private String locationName;

    @Column(name = "location_latitude")
    private Double locationLatitude;

    @Column(name = "location_longitude")
    private Double locationLongitude;

    @Column(name = "location_type")
    private String locationType; // "city", "current", "custom"

    @Builder.Default
    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Builder.Default
    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (likeCount == null)
            likeCount = 0;
        if (commentCount == null)
            commentCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method for displaying time in Facebook-style format
    public String getTimeAgo() {
        return TimeUtil.getTimeAgo(this.createdAt);
    }

    // Helper method for verbose time display
    public String getVerboseTimeAgo() {
        return TimeUtil.getVerboseTimeAgo(this.createdAt);
    }
}