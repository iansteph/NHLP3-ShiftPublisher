package iansteph.nhlp3.shiftpublisher.proxy;

import iansteph.nhlp3.shiftpublisher.client.NhlDataClient;
import iansteph.nhlp3.shiftpublisher.model.roster.Roster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.String.format;

public class NhlDataProxy {

    private final NhlDataClient nhlDataClient;

    private static final Logger LOGGER = LogManager.getLogger(NhlDataProxy.class);

    public NhlDataProxy(final NhlDataClient nhlDataClient) {

        this.nhlDataClient = nhlDataClient;
    }

    public Roster getRosterForTeamId(final int teamId) {

        try {

            LOGGER.info(format("Retrieving roster for teamId %d", teamId));
            final Roster roster = nhlDataClient.getRosterForTeamId(teamId);
            LOGGER.info(format("Successfully retrieved roster for teamId %d", teamId));
            return roster;
        }
        catch (final Exception e) {

            LOGGER.error(e);
            throw e;
        }
    }
}
