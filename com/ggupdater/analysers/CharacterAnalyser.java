package com.ggupdater.analysers;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.ggupdater.hooks.ClassHook;
import com.ggupdater.hooks.FieldHook;
import com.ggupdater.main.GGUpdater;
import com.ggupdater.utils.InstructionSearcher;

public class CharacterAnalyser extends AbstractAnalyser {

	FieldInsnNode animation;
	
	@Override
	protected boolean canRun(ClassNode node) {

		if (node.superName.equals(GGUpdater.getClassHook("Entity").getClazzName())) {

			if ((node.access & Opcodes.ACC_PUBLIC) != 0 && (node.access & Opcodes.ACC_ABSTRACT) != 0 && (node.access & Opcodes.ACC_FINAL) == 0) {

				animation = getAnimation(node);
				
				return true;

			}

		}

		return false;
	}

	@Override
	protected void analyse(ClassNode node) {

		GGUpdater.HOOKS.put("Character", new ClassHook("Character", node.name));
		GGUpdater.HOOKS.put("Character.getAnimation", new FieldHook("Character.getAnimation", "getAnimation", node.name, animation.name, 0));

	}

	public FieldInsnNode getAnimation(ClassNode node) {

		ListIterator<MethodNode> mnIt = node.methods.listIterator();
		while (mnIt.hasNext()) {

			MethodNode mn = mnIt.next();

			InstructionSearcher searcher = new InstructionSearcher(mn.instructions, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.IMUL, Opcodes.ICONST_M1);
			if (searcher.match()) {

				for (AbstractInsnNode[] match : searcher.getMatches()) {
					
					FieldInsnNode fin = (FieldInsnNode) match[1];
					if (fin.desc.equals("I")) {
						return fin;
					}

				}

			}

		}
		return null;

	}

}
