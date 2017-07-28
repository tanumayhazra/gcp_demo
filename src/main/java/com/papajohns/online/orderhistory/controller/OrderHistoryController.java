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
    public ResponseEntity storeOrderDetail(@RequestBody PubsubMessage message){
        orderHistoryService.storeDetail(message);
        ResponseEntity response = new ResponseEntity(HttpStatus.OK);
        return response;
    }

    @RequestMapping(value = "/healthCheck", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String healthCheck(HttpServletRequest request, HttpServletResponse response){
        return "OKAY GOOGLE";
    }

    @RequestMapping(value = "/{orderNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody PubsubMessage getOrderDetail( HttpServletRequest request, HttpServletResponse response, @PathVariable("orderNumber") String orderNumber){
        PubsubMessage message = orderHistoryService.getOrderDetail(orderNumber);
        return  message;
    }

    /**
     * Method to send a response with a body
     *
     * @param response
     * @param status
     * @param contentType
     * @param body
     */
    protected void sendResponse(HttpServletResponse response, int status, String contentType, String body)  {
        try {
            response.setStatus(status);
            response.setContentType(contentType);
            response.getWriter().write(body);
        }
        catch (RuntimeException re) {
            throw re;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /*private String readPostData(HttpServletRequest request) {
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
    }*/

}
