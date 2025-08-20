package com.example.social_media_app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shares", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "post_id", "user_id" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "original_post_id", nullable = false)
    @JsonIgnoreProperties({ "shares", "comments", "likes", "user" })
    private Post originalPost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shared_post_id", nullable = false)
    @JsonIgnoreProperties({ "shares", "comments", "likes", "user" })
    private Post sharedPost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({ "posts", "comments", "likes", "shares", "password" })
    private User user;

    @Column(name = "share_text")
    private String shareText;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
