package iansteph.nhlp3.shiftpublisher.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import iansteph.nhlp3.shiftpublisher.model.request.ShiftPublisherRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShiftPublisherHandler implements RequestHandler<ShiftPublisherRequest, Object> {

    private static final Logger LOGGER = LogManager.getLogger(ShiftPublisherHandler.class);

    public Object handleRequest(final ShiftPublisherRequest shiftPublisherRequest, final Context context) {

        /*
         * TODO
         *  1. Call NHL TOI endpoint
         *  1.1. Archive the documents into an S3 Bucket
         *  2. Translate response into business object/DOM
         *  3. Loop through all player tables:
         *      3.1. Check for updated entries
         *      3.2. Collect shifts to be published
         *  4. Build shift events
         *  5. Publish shift events
         */
        return "Hello, world! 🌍";
    }
}
