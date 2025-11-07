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
    private String sportName;
    private LocalDateTime createdAt;
    private Integer views;
    private PostStatus postStatus;
    private PostUserDto postAuthor;
    private Boolean isBungae;
    private String bungaeLocation;
    private Integer maxParticipants;
    private Integer curParticipants;
    private String bungaeDatetime;
    private BungaeStatus bungaeStatus;
    private List<String> postImages;

    public static PostDetailDto fromEntity(Post post) {        
        return PostDetailDto.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .content(post.getContent())
            .sportName(post.getSport().getName())
            .createdAt(post.getCreatedAt())
            .views(post.getViews())
            .postAuthor(PostUserDto.builder()
                .authorId(post.getAuthor().getId())
                .authorUsername(post.getAuthor().getUsername())
                .authorNickname(post.getAuthor().getNickname())
                .authorProfileImgUrl(post.getAuthor().getProfileImgUrl())
                .build()
            )
            .isBungae(post.getIsBungae())
            .bungaeLocation(post.getBungaeLocation())
            .maxParticipants(post.getMaxParticipants())
            .curParticipants(post.getCurParticipants())
            .bungaeDatetime(post.getBungaeDatetime())
            .bungaeStatus(post.getBungaeStatus())
            .postImages(post.getPostImages().stream().map(PostImage::getImageUrl).toList())
            .build();
    }
}
