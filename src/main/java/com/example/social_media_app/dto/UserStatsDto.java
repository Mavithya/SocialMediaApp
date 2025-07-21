package com.example.social_media_app.dto;

import com.example.social_media_app.util.NumberFormatUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDto {
    private long postsCount;
    private long friendsCount;
    private long likesCount;
    
    public String getFormattedPostsCount() {
        return NumberFormatUtil.formatCount(postsCount);
    }
    
    public String getFormattedFriendsCount() {
        return NumberFormatUtil.formatCount(friendsCount);
    }
    
    public String getFormattedLikesCount() {
        return NumberFormatUtil.formatCount(likesCount);
    }
}
