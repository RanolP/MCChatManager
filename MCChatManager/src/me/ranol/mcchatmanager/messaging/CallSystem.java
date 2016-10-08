package me.ranol.mcchatmanager.messaging;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

public class CallSystem {
	public static boolean check(AsyncPlayerChatEvent e) {
		if (!e.getMessage().startsWith("@")) {
			return false;
		}
		boolean useReason = e.getMessage().contains(" ");
		String checked = useReason ? e.getMessage().split(" ")[0].substring(1)
				: e.getMessage().substring(1);
		Player p = Bukkit.getPlayer(checked);
		Messager.sendCenterMessage(p,
				"§c§l" + ChatColor.stripColor(e.getPlayer().getDisplayName())
						+ "§e님이 당신을 호출했습니다.");
		if (useReason)
			Messager.sendCenterMessage(
					p,
					"§a호출 사유: "
							+ e.getMessage()
									.replaceFirst(e.getMessage().split(" ")[0],
											"").trim());
		p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,
				1.0f, 1.0f);
		e.setCancelled(true);
		return true;
	}

	public static boolean complete(PlayerChatTabCompleteEvent e) {
		if (!e.getChatMessage().startsWith("@"))
			return false;
		if (e.getChatMessage().contains(" "))
			return false;
		String message = e.getChatMessage().split(" ")[0].substring(1).trim();
		List<String> list = new ArrayList<>();
		Bukkit.getOnlinePlayers().forEach((p) -> {
			if (p.getName().toLowerCase().contains(message.toLowerCase()))
				list.add("@" + p.getName());
		});
		e.getTabCompletions().clear();
		e.getTabCompletions().addAll(list);
		return true;
	}
}
