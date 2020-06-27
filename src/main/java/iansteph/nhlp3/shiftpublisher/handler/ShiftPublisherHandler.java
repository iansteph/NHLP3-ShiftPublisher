package iansteph.nhlp3.shiftpublisher.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import iansteph.nhlp3.shiftpublisher.model.request.ShiftPublisherRequest;

public class ShiftPublisherHandler implements RequestHandler<ShiftPublisherRequest, Object> {

    public Object handleRequest(final ShiftPublisherRequest shiftPublisherRequest, final Context context) {
        return "Hello, world! 🌍";
    }
}
