package com.papajohns.online.orderhistory.controller;

import com.papajohns.online.orderhistory.dao.OrderHistoryDao;
import com.papajohns.online.orderhistory.service.OrderHistoryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public void storeOrderDetail(HttpServletRequest request, HttpServletResponse response){
        String postData = readPostData(request);
        orderHistoryService.storeDetail(postData);
    }

    private String readPostData(HttpServletRequest request) {
        Reader reader = null;

        try {
            char [] characters = new char[4096];
            int charactersRead = 0;

            StringWriter writer = new StringWriter();
            reader = new BufferedReader(request.getReader());

            while ((charactersRead = reader.read(characters)) != -1) {
                writer.write(characters, 0, charactersRead);
            }

            return writer.toString();
        }
        catch (RuntimeException re) {
            throw re;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
        finally {
            if (null != reader) {
                try {
                    reader.close();
                }
                catch (Throwable t) {
                    // DO NOTHING
                }
            }
        }
    }

}
