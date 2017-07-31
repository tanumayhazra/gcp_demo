package com.papajohns.online.orderhistory.dao;


import com.google.cloud.datastore.*;
import com.google.pubsub.v1.PubsubMessage;
import com.papajohns.json.JSONObject;
import com.papajohns.json.JSONParser;
import com.papajohns.online.orderhistory.object.Message;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryDaoImpl implements OrderHistoryDao{
    private String messagesKind = "order";
    private KeyFactory keyFactory = getDatastoreInstance().newKeyFactory().setKind(messagesKind);

    @Override
    public void save(String message) {
        // Save message to "messages"
        Datastore datastore = getDatastoreInstance();
        if (message != null) {
            JSONObject jsonObjectEntity = JSONParser.parseAsJSONObjectTM(message);
            String orerNumber = jsonObjectEntity.getAsJSONObjectTM("payload").getAsString("orderNumber");

            Key key = datastore.allocateId(keyFactory.newKey(orerNumber));
            Entity.Builder messageEntityBuilder = Entity.newBuilder(key);
            messageEntityBuilder = messageEntityBuilder.set(Message.DATA, message);
            messageEntityBuilder = messageEntityBuilder.set(Message.PUBLISH_TIME, jsonObjectEntity.getAsString("timeStamp"));
            datastore.add(messageEntityBuilder.build());
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
