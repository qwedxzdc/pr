CREATE USER twitter_user WITH PASSWORD 'Snob2323';

DROP DATABASE IF EXISTS Tweet;

CREATE DATABASE "Tweet"
    WITH
    OWNER = twitter_user
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE TABLE user_info (
  user_id BIGSERIAL,
  user_name VARCHAR(30),
  email VARCHAR(30) UNIQUE,
  password VARCHAR(300),
  PRIMARY KEY (user_id)
);

CREATE TABLE tweet (
  tweet_id BIGSERIAL,
  user_id INTEGER,
  text VARCHAR(280),
  created_at TIMESTAMP,
  image VARCHAR(10485760),
  number_likes INTEGER,
  PRIMARY KEY (tweet_id),
  FOREIGN KEY (user_id) REFERENCES user_info (user_id)
);

CREATE TABLE subscribed (
 subscribed_id BIGSERIAL,
 user_id INTEGER,
 followed_user_id INTEGER,
 CONSTRAINT user_followed UNIQUE(user_id,followed_user_id),
 PRIMARY KEY (subscribed_id),
 FOREIGN KEY (user_id) REFERENCES user_info (user_id)
);

CREATE TABLE likes (
 likes BIGSERIAL,
 user_id INTEGER,
 tweet_id INTEGER,
 CONSTRAINT likes_tweet UNIQUE(user_id,tweet_id),
 PRIMARY KEY (likes),
 FOREIGN KEY (user_id) REFERENCES user_info (user_id)
);