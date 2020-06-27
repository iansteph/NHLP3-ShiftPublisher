package iansteph.nhlp3.shiftpublisher.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ShiftPublisherHandler implements RequestHandler<Object, Object> {

    public Object handleRequest(Object input, Context context) {
        return "Hello, world! 🌍";
    }
}
