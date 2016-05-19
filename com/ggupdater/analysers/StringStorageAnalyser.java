package com.ggupdater.analysers;

import java.util.HashMap;
import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.ggupdater.hooks.ClassHook;
import com.ggupdater.main.GGUpdater;

public class StringStorageAnalyser extends AbstractAnalyser {

	public static HashMap<String, String> StringStorageValues = new HashMap<String, String>();

	protected boolean canRun(ClassNode node) {
		int count = 0;
		ListIterator<FieldNode> li = node.fields.listIterator();
		while (li.hasNext()) {
			FieldNode fn = li.next();
			if (fn.desc.equals("Ljava/lang/String;"))
				count++;
		}
		return count > 20;
	}

	@Override
	protected void analyse(ClassNode node) {
		storeStrings(node);
		GGUpdater.HOOKS.put("StringStorage", new ClassHook("StringStorage", node.name));
	}

	private void storeStrings(ClassNode node) {
		ListIterator<MethodNode> mnli = node.methods.listIterator();
		while (mnli.hasNext()) {
			MethodNode mn = mnli.next();
			if (mn.name.equals("<clinit>")) {
				ListIterator<AbstractInsnNode> ainli = mn.instructions.iterator();
				while (ainli.hasNext()) {
					AbstractInsnNode ain = ainli.next();
					if (ain.getOpcode() == Opcodes.LDC) {
						String text = (String) ((LdcInsnNode) ain).cst;
						if (text == null)
							continue;
						if (text.length() < 4)
							continue;
						AbstractInsnNode next = ain.getNext();
						if (next.getOpcode() == Opcodes.PUTSTATIC) {
							FieldInsnNode fin = (FieldInsnNode) next;
							String[] theValues = new String[] { text };
							for (int i = 0; i < theValues.length; i++) {
								if (!StringStorageValues.containsKey(theValues[i])) {
									StringStorageValues.put(theValues[i], fin.name);
								}
							}
						}
					}
				}
			}
		}
	}

	public static String getStringStorageField(String lookupString) {
		if (StringStorageValues.containsKey(lookupString))
			return StringStorageValues.get(lookupString);
		return "";
	}

}