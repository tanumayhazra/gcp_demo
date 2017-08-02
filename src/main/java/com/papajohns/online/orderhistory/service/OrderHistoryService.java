package com.papajohns.online.orderhistory.service;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import com.papajohns.online.orderhistory.dao.OrderHistoryDao;
import com.papajohns.online.orderhistory.object.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.ListIterator;

@Service
public class OrderHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(OrderHistoryService.class);

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

    /*@Scheduled(fixedDelayString = "10000")
    public void subscriberCheck(){
        logger.info("Checking for subscriber ");
        try{
            String data = OrderDetailSubscriber.read();
            storeDetail(data);
        }catch (Exception e){
            logger.error("Pulling from subscriber failed");
        }

    }*/


    public void readSubscriber()throws Exception {
        String PROJECT_ID = ServiceOptions.getDefaultProjectId();
        TopicName topic = TopicName.create(PROJECT_ID, "my-topic-id");
        SubscriptionName subscription = SubscriptionName.create(PROJECT_ID, "my-topic-id");

        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            subscriptionAdminClient.createSubscription(subscription, topic, PushConfig.getDefaultInstance(), 0);
        }

        MessageReceiver receiver =
                new MessageReceiver() {
                    @Override
                    public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
                        logger.info("Received message: " + message.getData().toStringUtf8());
                        storeDetail(message.getData().toStringUtf8());
                        consumer.ack();
                    }
                };
        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.defaultBuilder(subscription, receiver).build();
            subscriber.addListener(
                    new Subscriber.Listener() {
                        @Override
                        public void failed(Subscriber.State from, Throwable failure) {
                            // Handle failure. This is called when the Subscriber encountered a fatal error and is shutting down.
                            System.err.println(failure);
                        }
                    },
                    MoreExecutors.directExecutor());
            subscriber.startAsync().awaitRunning();

            Thread.sleep(60000);
        } finally {
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }
    }

}
