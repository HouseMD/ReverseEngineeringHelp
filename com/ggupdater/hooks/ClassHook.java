package com.ggupdater.hooks;

public class ClassHook extends Hook {

	String clazzName;

	public ClassHook(String hookName, String clazzName) {
		super(hookName);
		this.clazzName = clazzName;
	}

	public String getClazzName() {
		return clazzName;
	}

}
