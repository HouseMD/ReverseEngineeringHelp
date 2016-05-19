package com.ggupdater.analysers;

import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.ggupdater.hooks.ClassHook;
import com.ggupdater.main.GGUpdater;

public class ClientAnalyser extends AbstractAnalyser {

	@Override
	protected boolean canRun(ClassNode node) {

		if (node.name.equals("client")) {

			return true;

		}

		return false;

	}

	@Override
	protected void analyse(ClassNode node) {

		GGUpdater.HOOKS.put("Client", new ClassHook("Client", "client"));

	}

	public static int getRevision(ClassNode node) {

		ListIterator<MethodNode> mnIt = node.methods.listIterator();
		while (mnIt.hasNext()) {

			MethodNode mn = mnIt.next();
			if (mn.name.equals("init")) {

				ListIterator<AbstractInsnNode> ainIt = mn.instructions.iterator();
				while (ainIt.hasNext()) {

					AbstractInsnNode ain = ainIt.next();
					if (ain instanceof IntInsnNode) {

						IntInsnNode iin = (IntInsnNode) ain;
						if (iin.operand == 765) {

							AbstractInsnNode ainNext = iin.getNext();
							if (ainNext instanceof IntInsnNode) {

								IntInsnNode iinNext = (IntInsnNode) ainNext;
								if (iinNext.operand == 503) {

									AbstractInsnNode ainRevision = iinNext.getNext();
									if (ainRevision instanceof IntInsnNode) {

										IntInsnNode iinRevision = (IntInsnNode) ainRevision;
										return iinRevision.operand;

									}

								}

							}

						}

					}

				}

			}

		}

		return 0;

	}

}
