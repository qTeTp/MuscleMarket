package com.example.muscle_market.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.muscle_market.domain.Post;
import com.example.muscle_market.domain.PostImage;
import com.example.muscle_market.enums.BungaeStatus;
import com.example.muscle_market.enums.PostStatus;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostDetailDto {
    private Long postId;
    private String title;
    private String content;
    private Long sportId;
    private String sportName;
    private LocalDateTime createdAt;
    private Integer views;
    private PostStatus postStatus;
    private SimplifiedUserDto postAuthor;
    private Boolean isBungae;
    private String bungaeLocation;
    private Integer maxParticipants;
    private Integer curParticipants;
    private String bungaeDatetime;
    private String bungaeStatus;
    private List<String> postImages;
    private SimplifiedPostDto prevPost;
    private SimplifiedPostDto nextPost;

    public static PostDetailDto fromEntity(Post post, Post prev, Post next) {        
        return PostDetailDto.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .content(post.getContent())
            .sportId(post.getSport().getId())
            .sportName(post.getSport().getName())
            .createdAt(post.getCreatedAt())
            .views(post.getViews())
            .postAuthor(SimplifiedUserDto.builder()
                .userId(post.getAuthor().getId())
                .username(post.getAuthor().getUsername())
                .nickname(post.getAuthor().getNickname())
                .profileImgUrl(post.getAuthor().getProfileImgUrl())
                .build()
            )
            .isBungae(post.getIsBungae())
            .bungaeLocation(post.getBungaeLocation())
            .maxParticipants(post.getMaxParticipants())
            .curParticipants(post.getCurParticipants())
            .bungaeDatetime(post.getBungaeDatetime())
            .bungaeStatus(post.getBungaeStatus().name())
            .postImages(post.getPostImages().stream().map(PostImage::getImageUrl).toList())
            .prevPost(prev == null ? null : new SimplifiedPostDto(prev))
            .nextPost(next == null ? null : new SimplifiedPostDto(next))
            .build();
    }
}
