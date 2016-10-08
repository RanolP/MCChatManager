package me.ranol.mcchatmanager.utils;

import java.lang.reflect.Constructor;

public class Singleton<T> {
	T instance;
	Class<T> clazz;

	/**
	 * @param clazz
	 *            - 생성시 Singleton<Class>(Class.class); 같이 생성해주세요.
	 */
	public Singleton(Class<T> clazz) {
		this.clazz = clazz;
	}

	public T getInstance() {
		if (instance == null) {
			synchronized (clazz) {
				try {
					Constructor<T> c = clazz.getDeclaredConstructor();
					c.setAccessible(true);
					instance = c.newInstance();
					c.setAccessible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return instance;
	}

	public void dispose() {
		instance = null;
		clazz = null;
	}
}
