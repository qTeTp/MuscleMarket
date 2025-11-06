package com.example.muscle_market.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.muscle_market.domain.Post;
import com.example.muscle_market.domain.PostImage;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostDetailDto {
    private Long postId;
    private String title;
    private String content;
    private String sportName;
    private Long bungaeId;
    private LocalDateTime createdAt;
    private PostUserDto postAuthor;
    private List<String> postImages;

    public static PostDetailDto fromEntity(Post post) {
        Long bungaeId = post.getBungae() == null ? null : post.getBungae().getBungaeId();
        
        return PostDetailDto.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .content(post.getContent())
            .sportName(post.getSport().getName())
            .bungaeId(bungaeId)
            .createdAt(post.getCreatedAt())
            .postAuthor(PostUserDto.builder()
                .authorId(post.getAuthor().getId())
                .authorUsername(post.getAuthor().getUsername())
                .authorNickname(post.getAuthor().getNickname())
                .authorProfileImgUrl(post.getAuthor().getProfileImgUrl())
                .build()
            )
            .postImages(post.getPostImages().stream().map(PostImage::getImageUrl).toList())
            .build();
    }
}
