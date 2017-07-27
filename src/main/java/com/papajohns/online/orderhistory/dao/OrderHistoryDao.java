package com.papajohns.online.orderhistory.dao;

import java.util.List;

public interface OrderHistoryDao {


    /** Save message to persistent storage. */
    void save(String message);

    /**
     * Retrieve most recent stored messages.
     * @param limit number of messages
     * @return list of messages
     */
    List<String> retrieve(int limit);

}
