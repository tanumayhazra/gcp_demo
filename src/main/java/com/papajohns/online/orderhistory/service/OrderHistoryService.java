package com.papajohns.online.orderhistory.service;

import com.papajohns.online.orderhistory.dao.OrderHistoryDao;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class OrderHistoryService {

    @Inject
    private OrderHistoryDao orderHistoryDao;

    public void storeDetail(String data){
        orderHistoryDao.save(data);
    }

}
