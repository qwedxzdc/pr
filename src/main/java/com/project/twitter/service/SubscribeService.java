package com.project.twitter.service;

import com.project.twitter.repository.SubscribeRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import static com.project.twitter.repository.SubscribeRepository.*;

@Service("subscribeService")
public class SubscribeService {
    private static final Logger log = Logger.getLogger(SubscribeService.class);

    private SubscribeRepository subscribeRepository = new SubscribeRepository();

    public void subscribe(Long userId,  Long initialUserId){
        try {
            if (isUserAlreadySubscribed(userId, initialUserId)) {
                subscribeRepository.unsubscribe(userId, initialUserId);
            } else {
                subscribeRepository.subscribe(userId, initialUserId);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            log.error("Error", ex);
        }
    }
}