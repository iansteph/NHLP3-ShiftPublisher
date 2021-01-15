package iansteph.nhlp3.shiftpublisher.client;

import iansteph.nhlp3.shiftpublisher.client.wrapper.JsoupWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.Optional;

import static java.lang.String.format;

public class NhlTimeOnIceClient {

    private static final Logger LOGGER = LogManager.getLogger(NhlTimeOnIceClient.class);
    private static final String TIME_ON_ICE_REPORT_VERSION_HISTORY_BUCKET = "nhlp3-shift-publisher-toi-report-version-history";

    private final JsoupWrapper jsoupWrapper;
    private final S3Client s3Client;

    public NhlTimeOnIceClient(final JsoupWrapper jsoupWrapper, final S3Client s3Client) {

        this.jsoupWrapper = jsoupWrapper;
        this.s3Client = s3Client;
    }

    public Optional<Document> getTeamToiReportForGame(final String season, final String teamAbbreviation, final String game) {

        final String resolvedUrl = format("http://www.nhl.com/scores/htmlreports/%s/T%s%s.HTM", season, teamAbbreviation, game);
        archiveTimeOnIceReportVersionHistory(season, teamAbbreviation, game, resolvedUrl);
        LOGGER.info(format("Retrieving TOI report for game %s in season %s for team %s via URL %s", game, season, teamAbbreviation, resolvedUrl));
        final Optional<Document> toiReport = jsoupWrapper.parseHtmlFromUrl(resolvedUrl);
        if (toiReport.isPresent()) {

            LOGGER.info(format("Successfully retrieved TOI report for game %s in season %s for team %s", game, season, teamAbbreviation));
        }
        return toiReport;
    }

    private void archiveTimeOnIceReportVersionHistory(
            final String season,
            final String teamAbbreviation,
            final String game,
            final String url
    ) {
        try {

            final Optional<String> rawToiReport = jsoupWrapper.getRawHtmlFromUrl(url);
            if (!rawToiReport.isPresent()) {

                throw new Exception("The raw TOI report was empty");
            }
            final String rawHtml = rawToiReport.get();
            final String gameIdSeasonComponent = season.substring(0, 4);
            final String gameId = format("%s%s", gameIdSeasonComponent, game);
            final String versionedS3ObjectKey = format("%s/T%s%s", gameId, teamAbbreviation, game);
            final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(TIME_ON_ICE_REPORT_VERSION_HISTORY_BUCKET)
                    .key(versionedS3ObjectKey)
                    .build();
            final RequestBody requestBody = RequestBody.fromString(rawHtml);
            final PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, requestBody);
        }
        catch (final Exception e) {

            LOGGER.warn(format("Failed archiving TimeOnIceReport to S3 version history bucket for URL: %s", url));
        }
    }
}
