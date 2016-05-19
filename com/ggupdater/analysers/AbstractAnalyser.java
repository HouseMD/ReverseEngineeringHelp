package com.ggupdater.analysers;

import org.objectweb.asm.tree.ClassNode;

public abstract class AbstractAnalyser {

	public void run(ClassNode node) {

		if (canRun(node)) {

			analyse(node);

		}

	}

	protected abstract boolean canRun(ClassNode node);

	protected abstract void analyse(ClassNode node);

}