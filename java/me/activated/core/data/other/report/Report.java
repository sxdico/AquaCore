package me.activated.core.data.other.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Report {

    private String reportedBy, date, reason, reporterServer, reportedServer;
    private long addedAt;
    private boolean solved;
    private String solvedBy = null;
}
