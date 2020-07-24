package me.activated.core.data.other.report;

import java.util.ArrayList;
import java.util.List;

public class ReportSerilization {

    public static String serilizeReports(List<Report> reports) {
        StringBuilder builder = new StringBuilder();
        for (Report report : reports) {
            builder.append(serilizeReport(report));
            builder.append(";");
        }
        return builder.toString();
    }

    public static List<Report> deserilizeReports(String source) {
        if (!source.contains(";")) return new ArrayList<>();

        List<Report> reports = new ArrayList<>();

        String[] attributes = source.split(";");
        for (String piece : attributes) {
            reports.add(deserilizeReport(piece));
        }

        return reports;
    }

    private static String serilizeReport(Report report) {

        if (report == null) return "null";

        return "reportedBy@" + report.getReportedBy() +
                ":reason@" + report.getReason() +
                ":reporterServer@" + report.getReporterServer() +
                ":reportedServer@" + report.getReportedServer() +
                ":addedAt@" + report.getAddedAt() +
                ":solved@" + report.isSolved() +
                (report.getSolvedBy() != null ? ":solvedBy@" + report.getSolvedBy() : "null") +
                ":date@" + report.getDate();
    }

    private static Report deserilizeReport(String source) {
        String reportedBy = "", reason = "", date = "", reporterServer = "", reportedServer = "", solvedBy = null;
        long addedAt = -1L;
        boolean solved = false;
        if (source.equals("null")) return null;

        String[] attributes = source.split(":");

        for (String info : attributes) {
            String[] reportAttributes = info.split("@");
            String data = reportAttributes[0];

            if (data.equalsIgnoreCase("reportedBy")) {
                reportedBy = reportAttributes[1];
            }
            if (data.equalsIgnoreCase("reason")) {
                reason = reportAttributes[1];
            }
            if (data.equalsIgnoreCase("date")) {
                date = reportAttributes[1];
            }
            if (data.equalsIgnoreCase("reporterServer")) {
                reporterServer = reportAttributes[1];
            }
            if (data.equalsIgnoreCase("addedAt")) {
                addedAt = Long.valueOf(reportAttributes[1]);
            }
            if (data.equalsIgnoreCase("reportedServer")) {
                reportedServer = reportAttributes[1];
            }
            if (data.equalsIgnoreCase("solved")) {
                solved = Boolean.valueOf(reportAttributes[1]);
            }
            if (data.equalsIgnoreCase("solvedBy")) {
                if (!reportAttributes[1].equalsIgnoreCase("null")) {
                    solvedBy = reportAttributes[1];
                }
            }
        }

        return new Report(reportedBy, date, reason, reporterServer, reportedServer, addedAt, solved, solvedBy);
    }
}
