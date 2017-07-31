package com.papajohns.online.orderhistory.controller;

import com.google.pubsub.v1.PubsubMessage;
import com.papajohns.online.orderhistory.dao.OrderHistoryDao;
import com.papajohns.online.orderhistory.service.OrderHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringWriter;

@RestController
@RequestMapping("/orderHistory/order")
public class OrderHistoryController {

    @Inject
    private OrderHistoryService orderHistoryService;

    @RequestMapping(value = "/store", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity storeOrderDetail(@RequestBody String message){
        orderHistoryService.storeDetail(message);
        ResponseEntity response = new ResponseEntity(HttpStatus.OK);
        return response;
    }

    @RequestMapping(value = "/healthCheck", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String healthCheck(HttpServletRequest request, HttpServletResponse response){
        return "OKAY GOOGLE";
    }

    @RequestMapping(value = "/{orderNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getOrderDetail( HttpServletRequest request, HttpServletResponse response, @PathVariable("orderNumber") String orderNumber){
        String message = orderHistoryService.getOrderDetail(orderNumber);
        return  message;
    }

    @RequestMapping(value = "/{startTime}/{endTime}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getOrderDetails( HttpServletRequest request, HttpServletResponse response, @PathVariable("startTime") String startTime, @PathVariable("endTime") String endTime){
        String message = orderHistoryService.getOrderDetails(startTime,endTime);
        return  message;
    }

}
