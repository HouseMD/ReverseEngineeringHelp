package com.ggupdater.hooks;

public class SuperClassHook extends Hook {

	String interfaceName;

	public SuperClassHook(String hookName, String interfaceName) {
		super(hookName);
		this.interfaceName = interfaceName;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

}
