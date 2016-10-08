package me.ranol.mcchatmanager;

import java.util.ArrayList;
import java.util.List;

import me.ranol.mcchatmanager.messaging.Messager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MCChatManager extends JavaPlugin {
	static List<String> compilers = new ArrayList<>();

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (c.getName().equalsIgnoreCase("chatcompile")) {
			if (compilers.contains(s.getName())) {
				s.sendMessage("Compile-Mode OFF: " + s.getName());
				compilers.remove(s.getName());
			} else {
				s.sendMessage("Compile-Mode ON: " + s.getName());
				compilers.add(s.getName());
			}
		}
		if (c.getName().equalsIgnoreCase("sources")) {
			ChatListener.source(s);
		}
		if (c.getName().equalsIgnoreCase("mcchat")) {
			if (s instanceof Player) {
				String msg = "";
				for (String b : a) {
					msg += " " + b;
				}
				Messager.sendCenterMessage((Player) s, msg.trim());
			}
		}
		return false;
	}
}
