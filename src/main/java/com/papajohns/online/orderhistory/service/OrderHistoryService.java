package com.papajohns.online.orderhistory.service;

import com.papajohns.online.orderhistory.dao.OrderHistoryDao;
import com.papajohns.online.orderhistory.object.Message;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.ListIterator;

@Service
public class OrderHistoryService {

    @Inject
    private OrderHistoryDao orderHistoryDao;

    public void storeDetail(String data){
        orderHistoryDao.save(data);
    }

    public String getOrderDetail(String orderNumber){
        Message message = orderHistoryDao.retrieve(orderNumber);
        return message.toString();
    }

    public String getOrderDetails(String startTime, String endTime){
        StringBuilder message = new StringBuilder("");
        List<Message> messages = orderHistoryDao.retrieve(startTime,endTime);
        ListIterator<Message> it = messages.listIterator();
        while(it.hasNext()){
            message.append("[");
            message.append(it.next());
            message.append("]");
        }
        return message.toString();
    }

}
