package git.codit.moderation.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportState {

	WAITING("Waiting"),
	DONE("Done");
	
	private String name;
	
}
