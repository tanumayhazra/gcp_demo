package com.papajohns.online.orderhistory.dao;


import com.google.cloud.datastore.*;
import com.papajohns.json.JSONObject;
import com.papajohns.json.JSONParser;
import com.papajohns.online.orderhistory.object.Message;
import com.papajohns.online.orderhistory.service.OrderHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryDaoImpl implements OrderHistoryDao{
    private static final Logger logger = LoggerFactory.getLogger(OrderHistoryDaoImpl.class);
    private String messagesKind = "order";
    private KeyFactory keyFactory = getDatastoreInstance().newKeyFactory().setKind(messagesKind);

    @Override
    public void save(String message) {
        // Save message to "messages"
        Datastore datastore = getDatastoreInstance();
        if (message != null) {
            JSONObject jsonObjectEntity = JSONParser.parseAsJSONObjectTM(message);
            String orderNumber = jsonObjectEntity.getAsJSONObjectTM("payload").getAsString("orderNumber");
            logger.info("Received Message with orderNumber "+orderNumber);
            IncompleteKey key = keyFactory.newKey(orderNumber);
            FullEntity<IncompleteKey> messageEntity = Entity.newBuilder(key)
                    .set(Message.DATA, StringValue.newBuilder(message).setExcludeFromIndexes(true).build())
                    .set(Message.PUBLISH_TIME, jsonObjectEntity.getAsString("timeStamp"))
                    .build();
            datastore.add(messageEntity);
        }
    }

    @Override
    public List<Message> retrieve(int limit) {
        // Get Message saved in Datastore
        Datastore datastore = getDatastoreInstance();
        Query<Entity> query =
                Query.newEntityQueryBuilder()
                        .setKind(messagesKind)
                        .setLimit(limit)
                        .addOrderBy(StructuredQuery.OrderBy.desc("publishTime"))
                        .build();
        QueryResults<Entity> results = datastore.run(query);

        List<Message> messages = new ArrayList<>();
        while (results.hasNext()) {
            Entity entity = results.next();
            Message message = new Message(entity.getKey().getName());
            String data = entity.getString(Message.DATA);
            if (data != null) {
                message.setData(data);
            }
            String publishTime = entity.getString(Message.PUBLISH_TIME);
            if (publishTime != null) {
                message.setPublishTime(publishTime);
            }
            messages.add(message);
        }
        return messages;
    }

    /**
     * Retrieve stored messages for given orderNumber
     *
     * @param orderNumber number of messages
     * @return Message
     */
    @Override
    public Message retrieve(String orderNumber) {
        logger.info("retrieving payload with orderNumber "+orderNumber);
        Entity messageEntity = getDatastoreInstance().get(keyFactory.newKey(orderNumber));
        return entityToMessage(messageEntity);
    }

    /**
     * Retrieve stored messages for given duration
     *
     * @param startTime start of the duration
     * @param endTime   end of the duration
     * @return Message
     */
    @Override
    public List<Message> retrieve(String startTime, String endTime) {
        List<Message>  messageList = new ArrayList<>();
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind(messagesKind)
                .setLimit(1)
                .setFilter(StructuredQuery.PropertyFilter.le(Message.PUBLISH_TIME, endTime))
                .addOrderBy(StructuredQuery.OrderBy.desc("publishTime"))
                .build();
        QueryResults<Entity> resultList = getDatastoreInstance().run(query);
        Cursor cursor = resultList.getCursorAfter();
        query = Query.newEntityQueryBuilder()
                .setKind(messagesKind)
                .setStartCursor(cursor)
                .setFilter(StructuredQuery.PropertyFilter.ge(Message.PUBLISH_TIME, startTime))
                .addOrderBy(StructuredQuery.OrderBy.desc("publishTime"))
                .build();
        resultList = getDatastoreInstance().run(query);
        while (resultList.hasNext()) {
            messageList.add(entityToMessage(resultList.next()));
        }
        return messageList;
    }

    private Message entityToMessage(Entity entity){
        return new Message.Builder()
                .orderNumber(entity.getKey().getName())
                .publishTime(entity.getString(Message.PUBLISH_TIME))
                .data(entity.getString(Message.DATA)).build();
    }

    private Datastore getDatastoreInstance() {
        return DatastoreOptions.getDefaultInstance().getService();
    }
}
