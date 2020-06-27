package iansteph.nhlp3.shiftpublisher.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import iansteph.nhlp3.shiftpublisher.model.request.ShiftPublisherRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShiftPublisherHandler implements RequestHandler<ShiftPublisherRequest, Object> {

    private static final Logger logger = LogManager.getLogger(ShiftPublisherHandler.class);

    public Object handleRequest(final ShiftPublisherRequest shiftPublisherRequest, final Context context) {
        return "Hello, world! 🌍";
    }
}
