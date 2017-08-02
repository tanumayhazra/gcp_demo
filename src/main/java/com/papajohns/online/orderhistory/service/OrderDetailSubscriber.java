package com.papajohns.online.orderhistory.service;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by 627053 on 8/2/2017.
 */
public class OrderDetailSubscriber {

    // use the default project id
    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

    private static final BlockingQueue<PubsubMessage> messages = new LinkedBlockingDeque<>();

    static class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            messages.offer(message);
            consumer.ack();
        }
    }

    public static String read() throws Exception {
        // set subscriber id, eg. my-sub
        String subscriptionId = "";
        PubsubMessage message = PubsubMessage.getDefaultInstance();
        SubscriptionName subscriptionName = SubscriptionName.create(PROJECT_ID, subscriptionId);
        Subscriber subscriber = null;
        try {
            // create a subscriber bound to the asynchronous message receiver
            subscriber = Subscriber.defaultBuilder(subscriptionName, new MessageReceiverExample())
                    .build();
            subscriber.startAsync().awaitRunning();
            message = messages.take();
        } finally {
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }
        return message.getData().toStringUtf8();
    }
}
