package com.ggupdater.utils;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;

//  * @author tommo

public class InstructionSearcher {

	private InsnList nodes;

	private int[] opcodes;

	private List<AbstractInsnNode[]> matches = new ArrayList<AbstractInsnNode[]>();

	private int highestBreakpoint = 0;

	private int highestBreakpointOpcode = -1;

	public InstructionSearcher(InsnList nodes, int... opcodes) {
		this.nodes = nodes;
		this.opcodes = new int[opcodes.length];
		for (int i = 0; i < opcodes.length; i++) {
			this.opcodes[i] = opcodes[i];
		}
		matches.add(new AbstractInsnNode[opcodes.length]);
	}

	private void clear() {
		for (int i = 0; i < matches.get(size() - 1).length; i++) {
			matches.get(size() - 1)[i] = null;
		}
	}

	public boolean match() {
		int matchIndex = 0;
		boolean anyMatches = false;

		AbstractInsnNode currentNode = nodes.getFirst();
		while (currentNode != null) {
			if (matchIndex == opcodes.length) {
				matches.add(new AbstractInsnNode[opcodes.length]);
				anyMatches = true;
				matchIndex = 0;
			}
			if (currentNode.getOpcode() == opcodes[matchIndex]) {
				matches.get(size() - 1)[matchIndex] = currentNode;
				matchIndex++;
			} else {
				if (matchIndex > highestBreakpoint) {
					highestBreakpoint = matchIndex;
					highestBreakpointOpcode = currentNode.getOpcode();
				}
				clear();
				matchIndex = 0;
			}
			if (matchIndex > 0 && currentNode instanceof JumpInsnNode) {
				JumpInsnNode jumpInsn = (JumpInsnNode) currentNode;
				currentNode = jumpInsn.label.getNext();
			} else {
				currentNode = currentNode.getNext();
			}
		}

		matches.remove(size() - 1);
		return anyMatches;
	}

	public List<AbstractInsnNode[]> getMatches() {
		return matches;
	}

	public int getHighestBreakpoint() {
		return highestBreakpoint;
	}

	public int getHighestBreakpointOpcode() {
		return highestBreakpointOpcode;
	}

	public int size() {
		return matches.size();
	}

}