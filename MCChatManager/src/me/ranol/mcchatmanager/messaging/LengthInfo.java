package me.ranol.mcchatmanager.messaging;

import java.util.HashMap;

import me.ranol.mcchatmanager.utils.Singleton;

public class LengthInfo {
	HashMap<Character, Byte> length = new HashMap<>();
	static Singleton<LengthInfo> singleton = new Singleton<>(LengthInfo.class);
	private static final byte DEFAULT = 4;
	{
		register('A', 'H', 5);
		register('I', 3);
		register('J', 'Z', 5);

		register('a', 'e', 5);
		register('f', 4);
		register('g', 'h', 5);
		register('i', 1);
		register('j', 5);
		register('k', 4);
		register('l', 1);
		register('m', 's', 5);
		register('t', 4);
		register('u', 'z', 5);
		register('0', '9', 5);
		register(' ', 3);
		registerArray(1, '!', ':', ';', '\'', '|', '.', ',');
		register('@', 6);
		registerArray(5, '#', '$', '%', '^', '&', '*', '-', '_', '+', '=', '?',
				'/', '\\', '~');
		registerArray(4, '(', ')', '{', '}', '<', '>');
		registerArray(3, '[', ']', '"');
		registerArray(2, '`');
		register('가', '힣', 10);
		register('ㄱ', 'ㅎ', 9);
		registerArray(9, 'ㄲ', 'ㄳ', 'ㄵ', 'ㄶ', 'ㄸ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ',
				'ㅀ', 'ㅃ', 'ㅄ', 'ㅆ', 'ㅉ');
	}

	public static LengthInfo getInstance() {
		return singleton.getInstance();
	}

	public void registerArray(byte length, char... register) {
		for (char c : register) {
			register(c, length);
		}
	}

	public void registerArray(int length, char... register) {
		registerArray((byte) length, register);
	}

	public byte getLength(char c) {
		if (length.containsKey(c)) {
			return length.get(c);
		}
		return DEFAULT;
	}

	public byte getBoldLength(char c) {
		return (byte) (getLength(c) + (c == ' ' ? 0 : 1));
	}

	public void register(char from, char to, byte length) {
		for (; from <= to; from++) {
			register(from, length);
		}
	}

	public void register(char from, char to, int length) {
		register(from, to, (byte) length);
	}

	public void register(char toSet, int length) {
		register(toSet, (byte) length);
	}

	public void register(char toSet, byte length) {
		this.length.put(toSet, length);
	}
}
