package com.ggupdater.hooks;

import java.util.HashMap;
import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.ggupdater.main.GGUpdater;
import com.ggupdater.utils.InstructionSearcher;

public class FieldHook extends Hook {

	String getterName;
	String returnParentClazzName;
	String returnFieldName;
	int multiplier = 0;

	public FieldHook(String hookName, String getterName, String returnParentClazzName, String returnFieldName, int multiplier) {
		super(hookName);
		this.getterName = getterName;
		this.returnParentClazzName = returnParentClazzName;
		this.returnFieldName = returnFieldName;
		if (multiplier != -1)
			this.multiplier = findMultiplier(returnFieldName);
	}

	public String getGetterName() {
		return getterName;
	}

	public String getReturnParentClazzName() {
		return returnParentClazzName;
	}

	public String getReturnFieldName() {
		return returnFieldName;
	}

	public int getMultiplier() {
		return multiplier;
	}

	private int findMultiplier(String fieldName) {

		HashMap<Integer, Integer> multiplierTally = new HashMap<>();

		for (String className : GGUpdater.CLASSES.keySet()) {

			ClassNode cn = GGUpdater.CLASSES.get(className);

			ListIterator<MethodNode> mnIt = cn.methods.listIterator();
			while (mnIt.hasNext()) {

				MethodNode mn = mnIt.next();

				searchPatternGLI(mn, multiplierTally, fieldName);

			}

		}

		int highestOccurance = 0;

		for (Integer value : multiplierTally.keySet()) {

			if (highestOccurance == 0)
				highestOccurance = value;

			if (multiplierTally.get(value) > multiplierTally.get(highestOccurance))
				highestOccurance = value;

		}

		if (highestOccurance != 0)
			return highestOccurance;

		return 0;

	}

	private void searchPatternGLI(MethodNode mn, HashMap<Integer, Integer> multiplierTally, String fieldName) {

		InstructionSearcher searcher = new InstructionSearcher(mn.instructions, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL);
		if (searcher.match()) {

			for (AbstractInsnNode[] match : searcher.getMatches()) {

				FieldInsnNode fin = (FieldInsnNode) match[0];
				if (fin.desc.equals("I")) {

					if (fin.name.equals(fieldName)) {

						if (match[1] instanceof LdcInsnNode) {

							LdcInsnNode lin = (LdcInsnNode) match[1];
							incrementMultiplierTally(multiplierTally, Integer.parseInt(lin.cst.toString()));

						}

					}

				}

			}

		}

	}

	private void incrementMultiplierTally(HashMap<Integer, Integer> multiplierTally, int value) {

		if (multiplierTally.containsKey(value)) {

			int tempAmount = multiplierTally.get(value);
			multiplierTally.remove(value);
			multiplierTally.put(value, tempAmount += 1);

		} else {

			multiplierTally.put(value, 1);

		}

	}

}
