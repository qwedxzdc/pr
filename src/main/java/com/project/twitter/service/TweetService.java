package com.project.twitter.service;

import com.project.twitter.entity.Tweet;
import com.project.twitter.repository.TweetRepository;
import com.project.twitter.repository.UserRepository;
import com.project.twitter.validator.ValidateResult;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.project.twitter.common.GeneralUtil.CHARSET;
import static com.project.twitter.common.GeneralUtil.defaultCurrentPage;
import static com.project.twitter.repository.SubscribeRepository.isUserAlreadySubscribed;
import static com.project.twitter.repository.UserRepository.getUserIdByEmail;
import static com.project.twitter.service.ImageService.ImageToByte;
import static com.project.twitter.service.ImageService.generateUUIDName;
import static com.project.twitter.validator.TweetValidator.getValidateImageResult;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

@Service("tweetService")
public class TweetService {
    private static final Logger log = Logger.getLogger(TweetService.class);
    private static final String CONTENT_TYPE = "text/html";
    private static final int REQUEST_FILE_IMAGE_INDEX = 1;
    private static final int REQUEST_FILE_TEXT_INDEX = 0;
    private static final String IMAGE_FOLDER = "C:\\Work\\";
    private Long loggedUserId = null;
    private boolean isSubscribed;
    private static final int RECORDS_PER_PAGE = 5;
    TweetRepository tweetRepository = new TweetRepository();

    public void deleteTweet(String email, Long userId, Long tweetId) {
        Long userIdByEmail = getUserIdByEmail(email);
        try {
            if (userId.equals(userIdByEmail)) {
                tweetRepository.deleteTweet(tweetId);
            } else {
                throw new Exception("Can not delete other user tweet");
            }
        } catch (Exception ex) {
            log.error("Exception", ex);
        }
    }

    public ModelMap likeTweet(Boolean like, Long tweetId, Long userId, Long initialUserId, Long currentPage) {
        ModelMap model = new ModelMap();
        try {
            TweetRepository tweetRepository = new TweetRepository();
            tweetRepository.likeTweet(like, tweetId, initialUserId);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("userId", userId);
        } catch (SQLException | ClassNotFoundException ex) {
            log.error("Exception", ex);
        }
            return model;
    }

    public ModelMap getTweetsPerPage(String email, Long userId, Integer currentPage) {
        ModelMap model = new ModelMap();
        try {
            loggedUserId = getUserIdByEmail(email);
            if (userId == null || userId.equals(loggedUserId)) {
                model = getTweets(currentPage, loggedUserId, true);
            } else {
                isSubscribed = isUserAlreadySubscribed(userId, loggedUserId);
                model = getTweets(currentPage, userId, false);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            log.error("Error", ex);
        }
        return model;
    }

    public ModelMap postTweet(HttpServletRequest request,
                              HttpServletResponse response,
                              String email,
                              Integer currentPage) {
        ModelMap model = new ModelMap();
        try {
            Long userIdByEmail = getUserIdByEmail(email);
            Map<String, String> messages = new HashMap<>();
            Tweet tweetDto = new Tweet();
            response.setContentType(CONTENT_TYPE);
            boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
            if (!isMultipartContent) {
                log.error("Content is not multipart. Please contact to admin.");
            //    messages.put("tweet", "Content is not multipart. Please contact to admin.");
                model.addAttribute("messages", "Content is not multipart. Please contact to admin.");
            }
            List<FileItem> fields = getFileItems(request);
            FileItem fileItemImage = fields.get(REQUEST_FILE_IMAGE_INDEX);
            FileItem fileItemText = fields.get(REQUEST_FILE_TEXT_INDEX);
            if (fileItemImage.getSize() == 0) {
                messages = saveText(fileItemText, tweetDto, userIdByEmail);
            } else {
                ValidateResult imageValid = getValidateImageResult(fileItemImage);
                if (imageValid.isValid()) {
                    tweetDto.setImage(getImageFromItem(fileItemImage));
                    messages = saveText(fileItemText, tweetDto, userIdByEmail);
                }
                messages.putAll(imageValid.getMessages());
            }
            model.addAttribute("messages", messages);
            model = getTweets(currentPage, loggedUserId, true);
        } catch (Exception ex) {
            log.error("Exeption", ex);
        }
        return model;
    }

    private ModelMap getTweets(Integer currentPage, Long userId, boolean isCurrentUserPage) {
        ModelMap model = new ModelMap();
        if (currentPage == null) {
            currentPage = defaultCurrentPage;
        } else {
            currentPage = Integer.valueOf(escapeHtml4(String.valueOf(currentPage)));
        }
        UserRepository userRepository = new UserRepository();
        try {
            model.addAttribute("notFollowedUsers", userRepository.getNotFollowedUsers(loggedUserId));
            model.addAttribute("followedUsers", userRepository.getFollowedUsers(loggedUserId));
            model.addAttribute("tweets",
                    userRepository.getTweetsForUser(currentPage, RECORDS_PER_PAGE, userId, isCurrentUserPage,
                            loggedUserId));
            int rows = (isCurrentUserPage) ? userRepository.getNumberOfRowsForInitial(userId) :
                    userRepository.getNumberOfRows(userId);
            model.addAttribute("noOfPages", getNumberOfPages(rows));
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("recordsPerPage", RECORDS_PER_PAGE);
            model.addAttribute("initialUserId", loggedUserId);
            model.addAttribute("userId", userId);
            if (!isCurrentUserPage) {
                model.addAttribute("isSubscribed", isSubscribed);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            log.error("Error", ex);
        }
        return model;
    }

    private Map<String, String> saveText(FileItem fileItemText, Tweet tweetDto, Long userIdByEmail)
            throws IOException {
        TweetRepository tweetRepository = new TweetRepository();
        InputStream stream = fileItemText.getInputStream();
        String tweet = Streams.asString(stream, CHARSET);
        tweetDto.setText(tweet);
        return tweetRepository.saveTweet(tweetDto, userIdByEmail);
    }


    private String getImageFromItem(FileItem fileItemImage) throws Exception {
        String imageName = generateUUIDName(fileItemImage);
        File file = new File(IMAGE_FOLDER + imageName);
        fileItemImage.write(file);
        byte[] bytes = ImageToByte(file);
        byte[] encodeBase64 = Base64.getEncoder().encode(bytes);
        return new String(encodeBase64, CHARSET);
    }

    private List<FileItem> getFileItems(HttpServletRequest request) throws FileUploadException {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding(CHARSET);
        return upload.parseRequest(request);
    }

    private int getNumberOfPages(int rows) {
        int numberOfPages = rows / RECORDS_PER_PAGE;
        if (rows % RECORDS_PER_PAGE > 0) {
            numberOfPages++;
        }
        if (rows <= RECORDS_PER_PAGE) {
            numberOfPages = 0;
        }
        return numberOfPages;
    }
}