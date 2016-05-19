package com.ggupdater.analysers;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.ggupdater.hooks.ClassHook;
import com.ggupdater.hooks.FieldHook;
import com.ggupdater.main.GGUpdater;
import com.ggupdater.utils.InstructionSearcher;

public class EntityAnalyser extends AbstractAnalyser {

	FieldInsnNode modelHeight;

	@Override
	protected boolean canRun(ClassNode node) {

		boolean isAbstract = false;
		int nonStaticIntCount = 0;
		if (node.superName.equals(GGUpdater.getClassHook("DualNode").getClazzName())) {

			if ((node.access & Opcodes.ACC_ABSTRACT) != 0)
				isAbstract = true;

			ListIterator<FieldNode> fnIt = node.fields.listIterator();
			while (fnIt.hasNext()) {

				FieldNode fn = fnIt.next();
				if (isAbstract)
					if (fn.desc.equals("I") && (fn.access & Opcodes.ACC_STATIC) == 0 && (fn.access & Opcodes.ACC_PUBLIC) != 0) {
						nonStaticIntCount++;
					}

			}

		}

		if (nonStaticIntCount == 1 && isAbstract) {

			modelHeight = getModelHeight(node);

			return true;

		}

		return false;
	}

	@Override
	protected void analyse(ClassNode node) {

		GGUpdater.HOOKS.put("Entity", new ClassHook("Entity", node.name));
		GGUpdater.HOOKS.put("Entity.getModelHeight", new FieldHook("Entity.getModelHeight", "getModelHeight", node.name, modelHeight.name, 0));

	}

	public FieldInsnNode getModelHeight(ClassNode node) {

		ListIterator<MethodNode> mnIt = node.methods.listIterator();
		while (mnIt.hasNext()) {

			MethodNode mn = mnIt.next();
			if (mn.name.equals("<init>")) {

				InstructionSearcher searcher = new InstructionSearcher(mn.instructions, Opcodes.ALOAD, Opcodes.LDC, Opcodes.PUTFIELD);
				if (searcher.match()) {

					for (AbstractInsnNode[] match : searcher.getMatches()) {

						FieldInsnNode fin = (FieldInsnNode) match[2];

						return fin.desc.equals("I") ? fin : null;

					}

				}

			}

		}

		return null;

	}
}
