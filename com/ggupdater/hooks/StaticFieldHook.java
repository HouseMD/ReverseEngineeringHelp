package com.ggupdater.hooks;

public class StaticFieldHook extends FieldHook {

	String returnStaticClazzName;

	public StaticFieldHook(String hookName, String getterName, String returnParentClazzName, String returnStaticClazzName, String returnFieldName, int multiplier) {
		super(hookName, getterName, returnParentClazzName, returnFieldName, multiplier);
		this.returnStaticClazzName = returnStaticClazzName;
	}

	public String getReturnStaticClazzName() {
		return returnStaticClazzName;
	}

}
