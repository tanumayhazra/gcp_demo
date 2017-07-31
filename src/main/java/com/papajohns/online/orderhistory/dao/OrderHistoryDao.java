package com.papajohns.online.orderhistory.dao;

import com.google.pubsub.v1.PubsubMessage;
import com.papajohns.online.orderhistory.object.Message;

import java.util.List;

public interface OrderHistoryDao {


    /** Save message to persistent storage. */
    void save(String message);

    /**
     * Retrieve most recent stored messages.
     * @param limit number of messages
     * @return list of messages
     */
    List<Message> retrieve(int limit);

    /**
     * Retrieve stored message for given orderNumber
     * @param orderNumber order NUmber
     * @return Message
     */
    Message retrieve(String orderNumber);

    /**
     * Retrieve stored messages for given duration
     * @param startTime start of the duration
     * @param endTime end of the duration
     * @return Message
     */
    List<Message> retrieve(String startTime, String endTime);

}
