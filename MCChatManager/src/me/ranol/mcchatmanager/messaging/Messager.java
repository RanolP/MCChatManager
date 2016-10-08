package me.ranol.mcchatmanager.messaging;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messager {
	// SpigotMC의 SirSpoodle님이 만든 코드를 수정하였습니다.
	// SpigotMC:
	// https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
	// PasteBin1: http://pastebin.com/BADd6K43 [실제 사용 함수]
	// PasteBin2: http://pastebin.com/9Be2DF2z [길이 저장]
	private static final int CENTER_PIXEL = 154;

	public static void sendCenterMessage(Player player, String message) {
		// 메시지가 없으면 그냥 날리고 끝
		if (message == null || message.trim().equals("")) {
			player.sendMessage("");
			return;
		}
		int messagePixelSize = 0;
		// 색 코드 체크
		boolean colored = false;
		// 굵음 여부
		boolean bold = false;
		for (char c : message.toCharArray()) {
			// 색 코드라면
			if (c == '§' || c == '&') {
				colored = true;
				continue;
			} else if (colored) {
				colored = false;
				// 굵음 코드라면
				if (c == 'L' || c == 'l') {
					bold = true;
					continue;
					// 굵음 해제 안함
				} else if (c == 'o' || c == 'O' || c == 'm' || c == 'M'
						|| c == 'n' || c == 'N') {
					continue;
				} else {
					// 굵음 해제
					bold = false;
					continue;
				}
			} else {
				if (colored)
					colored = false;
				// 굵음 여부에 따라 가져옴
				messagePixelSize += bold ? LengthInfo.getInstance()
						.getBoldLength(c) : LengthInfo.getInstance().getLength(
						c);
				messagePixelSize++;
				System.out.println("굵음: " + bold + ", " + c);
			}
		}
		// 절반으로 나눔
		int halfSize = messagePixelSize / 2;
		// 보정 작업
		halfSize = CENTER_PIXEL - halfSize;
		// 공백의 길이를 가져옴
		int spaceSize = LengthInfo.getInstance().getLength(' ') + 1;
		int complete = 0;
		StringBuilder builder = new StringBuilder();
		while (complete < halfSize) {
			builder.append(" ");
			complete += spaceSize;
		}
		System.out.println("Builder Length: " + builder.toString().length());
		player.sendMessage(builder.toString()
				+ ChatColor.translateAlternateColorCodes('&', message));
	}
}
