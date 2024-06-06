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
        if (message instanceof Request request) {
            request.setData(null);
            return request;
        } else if (message instanceof Response response) {
            response.setResult(null);
            return response;
        }
        return message;
    }

}
