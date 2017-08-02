package com.papajohns.online.orderhistory.config;

import com.papajohns.online.orderhistory.dao.OrderHistoryDao;
import com.papajohns.online.orderhistory.dao.OrderHistoryDaoImpl;
import com.papajohns.online.orderhistory.service.OrderHistoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean(name = "orderHistoryDao")
    public OrderHistoryDao orderHistoryDao() {
        OrderHistoryDao orderHistoryDao = new OrderHistoryDaoImpl();
        return orderHistoryDao;
    }

    @Bean(name = "orderHistoryService")
    public OrderHistoryService orderHistoryService() {
        OrderHistoryService orderHistoryService = new OrderHistoryService();
        return orderHistoryService;
    }
}
