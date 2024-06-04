package com.ethan.remoting.util;

import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;

public class PayloadDropper {

    /**
     * For security consideration.
     *
     * @param message Request message
     * @return Message without data
     */
    public static Object getRequestWithoutData(Object message) {
        if (message instanceof Request) {
            Request request = (Request) message;
            request.setData(null);
            return request;
        } else if (message instanceof Response) {
            Response response = (Response) message;
            response.setResult(null);
            return response;
        }
        return message;
    }

}
