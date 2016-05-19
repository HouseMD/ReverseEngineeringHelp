package com.ggupdater.analysers;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import com.ggupdater.hooks.ClassHook;
import com.ggupdater.main.GGUpdater;

public class LinkedListAnalyser extends AbstractAnalyser {

	@Override
	protected boolean canRun(ClassNode node) {

		int fieldCount = 0;
		int nodeCount = 0;
		ListIterator<FieldNode> fnIt = node.fields.listIterator();
		while (fnIt.hasNext()) {

			fieldCount++;
			FieldNode fn = fnIt.next();
			if (((fn.access & Opcodes.ACC_STATIC) == 0)) {

				if (fn.desc.equals(String.format("L%s;", GGUpdater.getClassHook("Node").getClazzName())))
					nodeCount++;

			}

		}

		return nodeCount == 2 && fieldCount == 2;
	}

	@Override
	protected void analyse(ClassNode node) {

		GGUpdater.HOOKS.put("LinkedList", new ClassHook("LinkedList", node.name));

	}

}
