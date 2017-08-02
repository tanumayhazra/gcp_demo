package com.papajohns.online.orderhistory.object;

/**
 * A message captures information from the Pubsub message received over the push endpoint and is
 * persisted in storage.
 */
public class Message {
    private String orderNumber;
    private String publishTime;
    private String data;
   /* private String eventId;
    private String businessDate;
    private String orderDate;
    private String headerId;
    private String orderType;
    private String methodOfPayment;*/

    public Message(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Message(Builder builder) {
        this.orderNumber = builder.orderNumber;
        this.data = builder.data;
        this.publishTime = builder.publishTime;
    }

    public static final String DATA = "data";
    public static final String PUBLISH_TIME = "publishTime";

    public static class Builder {
        private String orderNumber;
        private String publishTime;
        private String data;

        public Builder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public Builder publishTime(String publishTime) {
            this.publishTime = publishTime;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Message build(){
            return new Message(this);
        }
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     * Method to convert the object to a JSON String
     *
     * @return
     */
    public String toString() {
        String result = "{ objectNumber : " +orderNumber+ ", data : "
                +data+", publishTime : " +publishTime+ "}";

        return result;
    }
}