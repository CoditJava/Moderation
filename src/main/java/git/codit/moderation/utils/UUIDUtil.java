package git.codit.moderation.utils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDUtil {
	
	public static List<UUID> loadList(List<String> uuids) {
		return uuids.stream().map(UUID::fromString).collect(Collectors.toList());
	}
	
	public static List<String> readUUIDList(List<UUID> uuids) {
		return uuids.stream().map(UUID::toString).collect(Collectors.toList());
	}
	
}
