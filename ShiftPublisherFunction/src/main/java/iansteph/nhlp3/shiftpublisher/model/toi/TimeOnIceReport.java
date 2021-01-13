package iansteph.nhlp3.shiftpublisher.model.toi;

import java.util.List;
import java.util.Objects;

public class TimeOnIceReport {

    private String visitorTeamName;
    private String homeTeamName;
    private Integer visitorTeamId;
    private Integer homeTeamId;
    private Integer visitorTeamScore;
    private Integer homeTeamScore;
    private Integer visitorTeamGameNumber;
    private Integer visitorTeamAwayGameNumber;
    private Integer homeTeamGameNumber;
    private Integer homeTeamHomeGameNumber;
    private String date;
    private String attendance;
    private String venueName;
    private String startTime;
    private String endTime;
    private String nhlGameNumber;
    private String gameState;
    private List<PlayerTimeOnIceReport> playerTimeOnIceReports;

    public String getVisitorTeamName() {

        return visitorTeamName;
    }

    public void setVisitorTeamName(final String visitorTeamName) {

        this.visitorTeamName = visitorTeamName;
    }

    public String getHomeTeamName() {

        return homeTeamName;
    }

    public void setHomeTeamName(final String homeTeamName) {

        this.homeTeamName = homeTeamName;
    }

    public Integer getVisitorTeamId() {

        return visitorTeamId;
    }

    public void setVisitorTeamId(final Integer visitorTeamId) {

        this.visitorTeamId = visitorTeamId;
    }

    public Integer getHomeTeamId() {

        return homeTeamId;
    }

    public void setHomeTeamId(final Integer homeTeamId) {

        this.homeTeamId = homeTeamId;
    }

    public Integer getVisitorTeamScore() {

        return visitorTeamScore;
    }

    public void setVisitorTeamScore(final Integer visitorTeamScore) {

        this.visitorTeamScore = visitorTeamScore;
    }

    public Integer getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setHomeTeamScore(final Integer homeTeamScore) {

        this.homeTeamScore = homeTeamScore;
    }

    public Integer getVisitorTeamGameNumber() {

        return visitorTeamGameNumber;
    }

    public void setVisitorTeamGameNumber(final Integer visitorTeamGameNumber) {

        this.visitorTeamGameNumber = visitorTeamGameNumber;
    }

    public Integer getVisitorTeamAwayGameNumber() {

        return visitorTeamAwayGameNumber;
    }

    public void setVisitorTeamAwayGameNumber(final Integer visitorTeamAwayGameNumber) {

        this.visitorTeamAwayGameNumber = visitorTeamAwayGameNumber;
    }

    public Integer getHomeTeamGameNumber() {

        return homeTeamGameNumber;
    }

    public void setHomeTeamGameNumber(final Integer homeTeamGameNumber) {

        this.homeTeamGameNumber = homeTeamGameNumber;
    }

    public Integer getHomeTeamHomeGameNumber() {

        return homeTeamHomeGameNumber;
    }

    public void setHomeTeamHomeGameNumber(final Integer homeTeamHomeGameNumber) {

        this.homeTeamHomeGameNumber = homeTeamHomeGameNumber;
    }

    public String getDate() {

        return date;
    }

    public void setDate(final String date) {

        this.date = date;
    }

    public String getAttendance() {

        return attendance;
    }

    public void setAttendance(final String attendance) {
        this.attendance = attendance;
    }

    public String getVenueName() {

        return venueName;
    }

    public void setVenueName(final String venueName) {

        this.venueName = venueName;
    }

    public String getStartTime() {

        return startTime;
    }

    public void setStartTime(final String startTime) {

        this.startTime = startTime;
    }

    public String getEndTime() {

        return endTime;
    }

    public void setEndTime(final String endTime) {

        this.endTime = endTime;
    }

    public String getNhlGameNumber() {

        return nhlGameNumber;
    }

    public void setNhlGameNumber(final String nhlGameNumber) {

        this.nhlGameNumber = nhlGameNumber;
    }

    public String getGameState() {

        return gameState;
    }

    public void setGameState(final String gameState) {

        this.gameState = gameState;
    }

    public List<PlayerTimeOnIceReport> getPlayerTimeOnIceReports() {

        return playerTimeOnIceReports;
    }

    public void setPlayerTimeOnIceReports(final List<PlayerTimeOnIceReport> playerTimeOnIceReports) {

        this.playerTimeOnIceReports = playerTimeOnIceReports;
    }

    @Override
    public String toString() {

        return "TimeOnIceReport{" +
                "visitorTeamName='" + visitorTeamName + '\'' +
                ", homeTeamName='" + homeTeamName + '\'' +
                ", visitorTeamId='" + visitorTeamId + '\'' +
                ", homeTeamId='" + homeTeamId + '\'' +
                ", visitorTeamScore=" + visitorTeamScore +
                ", homeTeamScore=" + homeTeamScore +
                ", visitorTeamGameNumber=" + visitorTeamGameNumber +
                ", visitorTeamAwayGameNumber=" + visitorTeamAwayGameNumber +
                ", homeTeamGameNumber=" + homeTeamGameNumber +
                ", homeTeamHomeGameNumber=" + homeTeamHomeGameNumber +
                ", date=" + date +
                ", attendance=" + attendance +
                ", venueName='" + venueName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", nhlGameNumber='" + nhlGameNumber + '\'' +
                ", gameState='" + gameState + '\'' +
                ", playerTimeOnIceReports=" + playerTimeOnIceReports +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeOnIceReport that = (TimeOnIceReport) o;
        return Objects.equals(visitorTeamName, that.visitorTeamName) &&
                Objects.equals(homeTeamName, that.homeTeamName) &&
                Objects.equals(visitorTeamId, that.visitorTeamId) &&
                Objects.equals(homeTeamId, that.homeTeamId) &&
                Objects.equals(visitorTeamScore, that.visitorTeamScore) &&
                Objects.equals(homeTeamScore, that.homeTeamScore) &&
                Objects.equals(visitorTeamGameNumber, that.visitorTeamGameNumber) &&
                Objects.equals(visitorTeamAwayGameNumber, that.visitorTeamAwayGameNumber) &&
                Objects.equals(homeTeamGameNumber, that.homeTeamGameNumber) &&
                Objects.equals(homeTeamHomeGameNumber, that.homeTeamHomeGameNumber) &&
                Objects.equals(date, that.date) &&
                Objects.equals(attendance, that.attendance) &&
                Objects.equals(venueName, that.venueName) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(nhlGameNumber, that.nhlGameNumber) &&
                Objects.equals(gameState, that.gameState) &&
                Objects.equals(playerTimeOnIceReports, that.playerTimeOnIceReports);
    }

    @Override
    public int hashCode() {

        return Objects.hash(visitorTeamName, homeTeamName, visitorTeamId, homeTeamId, visitorTeamScore, homeTeamScore, visitorTeamGameNumber, visitorTeamAwayGameNumber, homeTeamGameNumber, homeTeamHomeGameNumber, date, attendance, venueName, startTime, endTime, nhlGameNumber, gameState, playerTimeOnIceReports);
    }
}
