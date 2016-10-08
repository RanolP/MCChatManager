package me.ranol.mcchatmanager;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import me.ranol.mcchatmanager.messaging.CallSystem;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

public class ChatListener implements Listener {
	static HashMap<String, StringBuilder> sources = new HashMap<>();
	static HashMap<String, Integer> brackets = new HashMap<>();
	JavaCompiler compiler;
	static List<String> safeChars = new ArrayList<>();
	static HashMap<String, String> translate = new HashMap<>();
	private final String defaultSource = "import org.bukkit.*;"
			+ "\nimport java.*;" + "\npublic class test implements Runnable {"
			+ "\n\npublic void run(){";
	static {
		String test = "§6§l";
		// 예약어 등록
		translate.put("instanceof", test);
		translate.put("assert", test);
		translate.put("if", test);
		translate.put("else", test);
		translate.put("switch", test);
		translate.put("case", test);
		translate.put("default", test);
		translate.put("break", test);
		translate.put("goto", test);
		translate.put("return", test);
		translate.put("for", test);
		translate.put("while", test);
		translate.put("do", test);
		translate.put("continue", test);
		translate.put("new", test);
		translate.put("throw", test);
		translate.put("throws", test);
		translate.put("try", test);
		translate.put("catch", test);
		translate.put("finally", test);
		translate.put("this", test);
		translate.put("super", test);
		translate.put("extends", test);
		translate.put("implements", test);
		translate.put("import", test);
		translate.put("true", test);
		translate.put("false", test);
		translate.put("null", test);
		translate.put("package", test);
		translate.put("transient", test);
		translate.put("strictfp", test);
		translate.put("void", test);
		translate.put("char", test);
		translate.put("short", test);
		translate.put("int", test);
		translate.put("long", test);
		translate.put("double", test);
		translate.put("float", test);
		translate.put("const", test);
		translate.put("static", test);
		translate.put("volatile", test);
		translate.put("byte", test);
		translate.put("boolean", test);
		translate.put("class", test);
		translate.put("interface", test);
		translate.put("native", test);
		translate.put("private", test);
		translate.put("protected", test);
		translate.put("public", test);
		translate.put("final", test);
		translate.put("abstract", test);
		translate.put("synchronized", test);
		translate.put("enum", test);
		// 세이프 등록
		safeChars.add(" ");
		safeChars.add("\t");
		safeChars.add("(");
		safeChars.add(")");
		safeChars.add("{");
		safeChars.add("}");
		safeChars.add("[");
		safeChars.add("]");
		safeChars.add("<");
		safeChars.add(">");
		safeChars.add(",");
		safeChars.add(".");
		safeChars.add("?");
		safeChars.add(":");
		safeChars.add("/");
		safeChars.add("*");
		safeChars.add("-");
		safeChars.add("+");
		safeChars.add("!");
		safeChars.add("=");
	}

	public static void source(CommandSender s) {
		if (!sources.containsKey(s.getName())) {
			s.sendMessage(hl("//No Source here"));
			return;
		}
		for (String str : sources.get(s.getName()).toString().split("\n"))
			s.sendMessage(hl(str));
		for (int i = brackets.get(s.getName()); i > 0; i--)
			s.sendMessage(hl("}"));
	}

	private static String hl(String s) {
		if (s.startsWith("//"))
			s = "§3" + s;
		int index = 0;
		String staticString = "§o";
		while ((index = s.indexOf(".", index)) > -1) {
			int staticMethod = s.indexOf('(', index);
			if (staticMethod == -1)
				break;
			if (s.substring(index - 2, index - 1).equals(")")) {
				index += 1;
				continue;
			}
			String temp = s.substring(0, index) + staticString;
			temp += s.substring(index, staticMethod);
			temp += "§r";
			temp += s.substring(staticMethod);
			s = temp;
			index += staticMethod + 4;
		}
		index = 0;
		String quote = "§b";
		while ((index = s.indexOf("\"", index)) > -1) {
			int staticMethod = s.indexOf('"', index + 1);
			if (staticMethod == -1)
				break;
			staticMethod++;
			String temp = s.substring(0, index) + quote;
			temp += s.substring(index, staticMethod);
			temp += "§r";
			temp += s.substring(staticMethod);
			s = temp;
			index = staticMethod + 4;
		}
		for (String key : translate.keySet()) {
			if (!s.contains(key))
				continue;
			index = 0;
			String c = translate.get(key);
			while ((index = s.indexOf(key, index)) > -1) {
				if (index < 0)
					break;
				if (index != 0
						&& !safeChars.contains(s.substring(index - 1, index))) {
					index++;
					continue;
				}
				if (index + key.length() <= s.length()
						&& !safeChars
								.contains(s.substring(index + key.length(),
										index + key.length() + 1))) {
					index++;
					continue;
				}
				String temp = s.substring(0, index) + c;
				temp += s.substring(index, index + key.length());
				temp += "§r";
				temp += s.substring(index + key.length());
				s = temp;
				index += key.length() + c.length() + 2;
			}
		}
		return s;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		String n = e.getPlayer().getName();
		if (MCChatManager.compilers.contains(n)) {
			String source = e.getMessage();
			if (!sources.containsKey(n)) {
				sources.put(n, new StringBuilder(defaultSource));
				brackets.put(n, 2);
			}
			if (source.equalsIgnoreCase("!clean")) {
				sources.put(n, new StringBuilder(defaultSource));
				brackets.put(n, 2);
				e.setCancelled(true);
				e.getPlayer().sendMessage("§eCompiler§f: §aSource Cleaned.");
				return;
			}
			if (source.toLowerCase().startsWith("!import ")) {
				sources.get(n)
						.insert(0, "import " + source.substring(8) + "\n");
				e.setCancelled(true);
				e.getPlayer().sendMessage("§eCompiler§f: §aSource Imported.");
				return;
			}
			if (source.toLowerCase().startsWith("!var ")) {
				sources.get(n).insert(
						sources.get(n).indexOf("Runnable {\n") + 11,
						source.substring(5) + "\n");
				e.setCancelled(true);
				e.getPlayer().sendMessage(
						"§eCompiler§f: §aOutside Var Created.");
				return;
			}
			brackets.put(n, brackets.get(n) - containsCount(source, "}"));
			brackets.put(n, brackets.get(n) + containsCount(source, "{"));
			if (source.equalsIgnoreCase("!end")) {
				String get = sources.get(n).toString();
				for (int i = brackets.get(n); i > 0; i--)
					get += "\n}";
				e.getPlayer().sendMessage("§eCompiler§f: §aSource Compile...");
				compile(e.getPlayer(), get);
				e.setCancelled(true);
				return;
			}
			e.getPlayer().sendMessage("§eCompiler§f: §aSource Appended");
			sources.get(e.getPlayer().getName()).append("\n" + source);
			e.setCancelled(true);
			return;
		}
		if (CallSystem.check(e))
			return;
	}

	public int containsCount(String s, String b) {
		int count = 0;
		int index = 0;
		while ((index = s.indexOf(b, index)) != -1) {
			count++;
			index++;
		}
		return count;
	}

	public void compile(CommandSender s, String sources) {
		compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			s.sendMessage("§cCompiler Exception: Server's Java is JRE!");
			return;
		}
		StandardJavaFileManager sjfm = compiler.getStandardFileManager(null,
				null, null);
		File file = new File("test.java");
		try (PrintWriter writer = new PrintWriter(file)) {
			writer.println(sources);
		} catch (Exception e) {
		}
		Iterable<? extends JavaFileObject> fo = sjfm.getJavaFileObjects(file);
		if (!compiler.getTask(null, sjfm, null, null, null, fo).call()) {
			s.sendMessage("§cCompile Exception: Compile failed.");
			return;
		}
		try {
			URL[] urls = new URL[] { new File("").toURI().toURL() };
			try (URLClassLoader loader = new URLClassLoader(urls)) {
				Object o = loader.loadClass("test").newInstance();
				o.getClass().getMethod("run").invoke(o);
			}
		} catch (Exception e) {
		}
		if (file.exists())
			file.delete();
	}

	@EventHandler
	public void onTab(PlayerChatTabCompleteEvent e) {
		if (CallSystem.complete(e))
			return;
	}
}
