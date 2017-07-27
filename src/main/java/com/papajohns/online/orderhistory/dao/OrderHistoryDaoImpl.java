package com.papajohns.online.orderhistory.dao;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryDaoImpl implements OrderHistoryDao{
    private String messagesKind = "messages";
    private KeyFactory keyFactory = getDatastoreInstance().newKeyFactory().setKind(messagesKind);

    @Override
    public void save(String message) {
        // Save message to "messages"
        Datastore datastore = getDatastoreInstance();
        Key key = datastore.allocateId(keyFactory.newKey());

        Entity.Builder messageEntityBuilder = Entity.newBuilder(key)
                .set("messageId", message);

        /*if (message.getData() != null) {
            messageEntityBuilder = messageEntityBuilder.set("data", message.getData());
        }

        if (message.getPublishTime() != null) {
            messageEntityBuilder = messageEntityBuilder.set("publishTime", message.getPublishTime());
        }*/
        datastore.put(messageEntityBuilder.build());
    }

    @Override
    public List<String> retrieve(int limit) {
        // Get Message saved in Datastore
        Datastore datastore = getDatastoreInstance();
        Query<Entity> query =
                Query.newEntityQueryBuilder()
                        .setKind(messagesKind)
                        .setLimit(limit)
                        .addOrderBy(StructuredQuery.OrderBy.desc("publishTime"))
                        .build();
        QueryResults<Entity> results = datastore.run(query);

        List<String> messages = new ArrayList<>();
        while (results.hasNext()) {
            Entity entity = results.next();
            String message = new String(entity.getString("messageId"));
            /*String data = entity.getString("data");
            if (data != null) {
                message.setData(data);
            }
            String publishTime = entity.getString("publishTime");
            if (publishTime != null) {
                message.setPublishTime(publishTime);
            }*/
            messages.add(message);
        }
        return messages;
    }

    private Datastore getDatastoreInstance() {
        return DatastoreOptions.getDefaultInstance().getService();
    }
}
