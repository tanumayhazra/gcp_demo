package com.papajohns.online.orderhistory.service;

import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PubsubMessage;
import com.papajohns.online.orderhistory.dao.OrderHistoryDao;
import com.papajohns.online.orderhistory.object.Message;
import org.springframework.stereotype.Service;
import com.google.protobuf.ByteString;

import javax.inject.Inject;

@Service
public class OrderHistoryService {

    @Inject
    private OrderHistoryDao orderHistoryDao;

    public void storeDetail(PubsubMessage data){
        orderHistoryDao.save(data);
    }

    public PubsubMessage getOrderDetail(String orderNumber){
        PubsubMessage resultMessage = PubsubMessage.getDefaultInstance();
        Message message = orderHistoryDao.retrieve(orderNumber);
        return resultMessage.toBuilder()
                .setMessageId(message.getOrderNumber())
                .setData(ByteString.copyFromUtf8(message.getData()))
                .build();
    }

}
