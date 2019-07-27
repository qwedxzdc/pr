package com.project.twitter.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tweet")
public class Tweet {
    @Id
    @GeneratedValue
    private Long tweetId;
    private Long userId;
    private String userName;
    private String text;
    private LocalDateTime createdAt;
    private String image;
    private String formattedTime;
    private Long numberOfLikes;
    private boolean isLiked;
}