package com.ggupdater.analysers;

import java.util.ListIterator;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import com.ggupdater.hooks.ClassHook;
import com.ggupdater.main.GGUpdater;

public class DualNodeAnalyser extends AbstractAnalyser {

	@Override
	protected boolean canRun(ClassNode node) {

		int ownType = 0;

		if (node.superName.equals(GGUpdater.getClassHook("Node").getClazzName())) {

			ListIterator<FieldNode> fnIt = node.fields.listIterator();
			while (fnIt.hasNext()) {

				FieldNode fn = fnIt.next();
				if (fn.desc.equals(String.format("L%s;", node.name))) {

					ownType++;

				}

			}

		}

		return ownType == 2;

	}

	@Override
	protected void analyse(ClassNode node) {

		GGUpdater.HOOKS.put("DualNode", new ClassHook("DualNode", node.name));

	}
}
