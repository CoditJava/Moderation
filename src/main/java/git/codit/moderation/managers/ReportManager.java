package git.codit.moderation.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import git.codit.moderation.report.ReportData;

public class ReportManager {
	
	//private Moderation plugin;
	private Map<UUID, ReportData> reportDatas;
	
	public ReportManager() {
	//	this.plugin = Moderation.getInstance();
		this.reportDatas = new HashMap<UUID, ReportData>();
	}
	
	public void createReport(Player reported, Player reporter, String reason) {
		UUID reportID = newReportID();
		this.reportDatas.put(reportID, new ReportData(reportID, reported.getUniqueId(), reporter.getUniqueId(), reason));
	}

	public void destroyReport(UUID reportID) {
		this.reportDatas.remove(reportID);
	}
	
	public ReportData getReport(UUID reportID) {
		return this.reportDatas.get(reportID);
	}
	
	public Collection<ReportData> getAllReports() {
		return this.reportDatas.values();
	}
	
	private UUID newReportID() {
		UUID id = UUID.randomUUID();
		while(this.reportDatas.keySet().contains(id)) {
			id = UUID.randomUUID();
			return id;
		}
		return id;
	}
	
}
