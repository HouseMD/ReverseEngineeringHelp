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

public class NodeAnalyser extends AbstractAnalyser {

	FieldInsnNode previousNode;
	FieldNode nextNode;

	@Override
	protected boolean canRun(ClassNode node) {

		if (!node.superName.contains("java/lang/Object"))
			return false;

		int ownType = 0, longType = 0;
		ListIterator<FieldNode> fnIt = node.fields.listIterator();
		while (fnIt.hasNext()) {

			FieldNode fn = fnIt.next();
			if ((fn.access & Opcodes.ACC_STATIC) == 0) {

				if (fn.desc.equals(String.format("L%s;", node.name)))
					ownType++;

				if (fn.desc.equals("J"))
					longType++;

			}

		}

		if (ownType == 2 && longType == 1) {

			previousNode = getPreviousHook(node);
			fnIt = node.fields.listIterator();
			while (fnIt.hasNext()) {

				FieldNode fn = fnIt.next();
				if ((fn.access & Opcodes.ACC_STATIC) == 0) {

					if (fn.desc.equals(String.format("L%s;", node.name)))
						if (!fn.name.equals(previousNode.name))
							nextNode = fn;

				}

			}

			return true;

		}

		return false;

	}

	@Override
	protected void analyse(ClassNode node) {

		GGUpdater.HOOKS.put("Node", new ClassHook("Node", node.name));
		GGUpdater.HOOKS.put("Node.getPrevious", new FieldHook("Node.getPrevious", "getPrevious", node.name, previousNode.name, 0));
		GGUpdater.HOOKS.put("Node.getNext", new FieldHook("Node.getNext", "getNext", node.name, nextNode.name, 0));

	}

	public FieldInsnNode getPreviousHook(ClassNode node) {

		ListIterator<MethodNode> mnIt = node.methods.listIterator();
		while (mnIt.hasNext()) {

			MethodNode mn = mnIt.next();
			InstructionSearcher searcher = new InstructionSearcher(mn.instructions, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.IFNONNULL);
			if (searcher.match()) {

				for (AbstractInsnNode[] match : searcher.getMatches()) {

					FieldInsnNode fin = (FieldInsnNode) match[1];

					return fin.desc.equals(String.format("L%s;", node.name)) ? fin : null;

				}

			}

		}
		return null;

	}
}
