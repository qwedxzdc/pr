package com.project.twitter.validator;

import org.apache.commons.fileupload.FileItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetValidator {
    private final static int MIN_TWEET_LENGTH = 3;
    private final static int MAX_TWEET_LENGTH = 280;
    private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    private static final int VALID_FILE_SIZE = 1024 * 1024 * 10; //10MB
    private static final Pattern PATTERN = Pattern.compile(IMAGE_PATTERN);

    public static ValidateResult getValidateTextResult(String tweet) {
        ValidateResult validateResult = new ValidateResult();
        if (tweet == null || tweet.isEmpty()) {
            validateResult.setValid(false);
            validateResult.getMessages().put("tweet", "Cannot be empty");
        } else if (tweet.length() <= MIN_TWEET_LENGTH) {
            validateResult.setValid(false);
            validateResult.getMessages().put("tweet", "Must contains at least " + MIN_TWEET_LENGTH + " symbols");
        } else if (tweet.length() >= MAX_TWEET_LENGTH) {
            validateResult.setValid(false);
            validateResult.getMessages().put("tweet", "Maximum size is " + MAX_TWEET_LENGTH + " symbols");
        } else {
            validateResult.setValid(true);
        }
        return validateResult;
    }

    public static ValidateResult getValidateImageResult(FileItem fileItem) {
        ValidateResult validateResult = new ValidateResult();
        if (isImage(fileItem.getName())) {
            if (isFileSizeValid(fileItem)) {
                validateResult.setValid(true);
            } else {
                validateResult.setValid(false);
                validateResult.getMessages().put("tweet", "Please, add image with smaller size");
            }
        } else {
            validateResult.setValid(false);
            validateResult.getMessages().put("tweet", "Please, add image");
        }
        return validateResult;
    }

    private static boolean isFileSizeValid(FileItem fileItem) {
        return fileItem.getSize() < VALID_FILE_SIZE;
    }

    private static boolean isImage(String fileName) {
        Matcher matcher = PATTERN.matcher(fileName);
        return matcher.matches();
    }
}