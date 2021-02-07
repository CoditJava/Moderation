package git.codit.moderation.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatState {

	NORMAL("Normal"),
	SLOWED("Slowed"),
	MUTED("Muted"),
	RESTRICTED("Restricted");
	
	private String name;
	
}
