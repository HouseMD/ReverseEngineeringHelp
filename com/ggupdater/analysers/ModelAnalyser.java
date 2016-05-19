package com.ggupdater.analysers;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.ggupdater.hooks.ClassHook;
import com.ggupdater.hooks.FieldHook;
import com.ggupdater.main.GGUpdater;
import com.ggupdater.utils.InstructionSearcher;

public class ModelAnalyser extends AbstractAnalyser {

	FieldInsnNode vertexCount;
	FieldInsnNode indicesCount;
	FieldInsnNode texturedVertexCount;

	FieldInsnNode verticesX;
	FieldInsnNode verticesY;
	FieldInsnNode verticesZ;

	FieldInsnNode indicesX;
	FieldInsnNode indicesY;
	FieldInsnNode indicesZ;

	@Override
	protected boolean canRun(ClassNode node) {
		int count = 0;
		if (node.superName.equals(GGUpdater.getClassHook("Entity").getClazzName())) {

			ListIterator<MethodNode> mnIt = node.methods.listIterator();

			while (mnIt.hasNext()) {

				MethodNode mn = mnIt.next();
				if (mn.name.equals("<clinit>")) {

					ListIterator<AbstractInsnNode> ainIt = mn.instructions.iterator();
					while (ainIt.hasNext()) {

						AbstractInsnNode ain = ainIt.next();
						if (ain.getOpcode() == Opcodes.NEWARRAY) {

							if (((IntInsnNode) ain).operand == 4) {

								AbstractInsnNode prev = mn.instructions.get(ainIt.previousIndex() - 1);
								if (prev.getOpcode() == Opcodes.SIPUSH) {

									if (((IntInsnNode) prev).operand == 4096) {

										count++;

									}

								}

							}

						}

					}

				}

			}

		}

		if (count == 2) {

			vertexCount = getVertexCount(node);
			verticesX = getVerticesX(node);
			verticesY = getVerticesY(node);
			verticesZ = getVerticesZ(node);

			return true;

		}

		return false;
	}

	@Override
	protected void analyse(ClassNode node) {

		GGUpdater.HOOKS.put("Model", new ClassHook("Model", node.name));
		GGUpdater.HOOKS.put("Model.getVertexCount", new FieldHook("Model.getVertexCount", "getVertexCount", node.name, vertexCount.name, -1));
		GGUpdater.HOOKS.put("Model.getVerticesX", new FieldHook("Model.getVerticesX", "getVerticesX", node.name, verticesX.name, -1));
		GGUpdater.HOOKS.put("Model.getVerticesY", new FieldHook("Model.getVerticesY", "getVerticesY", node.name, verticesY.name, -1));
		GGUpdater.HOOKS.put("Model.getVerticesZ", new FieldHook("Model.getVerticesZ", "getVerticesZ", node.name, verticesZ.name, -1));

	}

	public FieldInsnNode getVertexCount(ClassNode node) {

		FieldInsnNode firstFound;

		ListIterator<MethodNode> mnIt = node.methods.listIterator();
		while (mnIt.hasNext()) {

			MethodNode mn = mnIt.next();

			InstructionSearcher firstSearcher = new InstructionSearcher(mn.instructions, Opcodes.ALOAD, Opcodes.ICONST_0, Opcodes.PUTFIELD);
			if (firstSearcher.match()) {

				for (AbstractInsnNode[] firstSearcherMatch : firstSearcher.getMatches()) {

					FieldInsnNode fin = (FieldInsnNode) firstSearcherMatch[2];

					if (fin.desc.equals("I")) {
						firstFound = fin;

						InstructionSearcher patternWithFirstSearcher = new InstructionSearcher(mn.instructions, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.IADD, Opcodes.IASTORE);
						if (patternWithFirstSearcher.match()) {

							for (AbstractInsnNode[] patternWithFirstSearcherMatch : patternWithFirstSearcher.getMatches()) {

								FieldInsnNode fin2 = (FieldInsnNode) patternWithFirstSearcherMatch[1];
								if (fin2.name.equals(firstFound.name)) {
									return firstFound;
								}

							}

						}

					}

				}

			}

		}
		return null;

	}

	public FieldInsnNode getVerticesX(ClassNode node) {

		ListIterator<MethodNode> mnIt = node.methods.listIterator();
		while (mnIt.hasNext()) {

			MethodNode mn = mnIt.next();

			InstructionSearcher searcher = new InstructionSearcher(mn.instructions, Opcodes.GETFIELD, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ALOAD, Opcodes.GETFIELD);
			if (searcher.match()) {

				for (AbstractInsnNode[] match : searcher.getMatches()) {

					FieldInsnNode fin = (FieldInsnNode) match[2];

					if (fin.name.equals(vertexCount.name)) {

						return ((FieldInsnNode) match[0]);

					}

				}

			}

		}
		return null;

	}

	public FieldInsnNode getVerticesY(ClassNode node) {

		ListIterator<MethodNode> mnIt = node.methods.listIterator();
		while (mnIt.hasNext()) {

			MethodNode mn = mnIt.next();

			InstructionSearcher searcher = new InstructionSearcher(mn.instructions, Opcodes.GETFIELD, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ALOAD, Opcodes.GETFIELD);
			if (searcher.match()) {

				for (AbstractInsnNode[] match : searcher.getMatches()) {

					FieldInsnNode fin = (FieldInsnNode) match[2];

					if (fin.name.equals(vertexCount.name) && !((FieldInsnNode) match[0]).name.equals(verticesX.name)) {

						return ((FieldInsnNode) match[0]);

					}

				}

			}

		}
		return null;

	}

	public FieldInsnNode getVerticesZ(ClassNode node) {

		ListIterator<MethodNode> mnIt = node.methods.listIterator();
		while (mnIt.hasNext()) {

			MethodNode mn = mnIt.next();

			InstructionSearcher searcher = new InstructionSearcher(mn.instructions, Opcodes.GETFIELD, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ALOAD, Opcodes.GETFIELD);
			if (searcher.match()) {

				for (AbstractInsnNode[] match : searcher.getMatches()) {

					FieldInsnNode fin = (FieldInsnNode) match[2];

					if (fin.name.equals(vertexCount.name) && !((FieldInsnNode) match[0]).name.equals(verticesX.name) && !((FieldInsnNode) match[0]).name.equals(verticesY.name)) {

						return ((FieldInsnNode) match[0]);

					}

				}

			}

		}
		return null;

	}

}

// VERTICIES
// AbstractInsnNode ain = ainIt.next();
// if (ain.getOpcode() == Opcodes.ALOAD) {
//
// ain = ainIt.next();
// if (ain.getOpcode() == Opcodes.GETFIELD)
// System.out.println(((FieldInsnNode) ain).name);
//
// }
