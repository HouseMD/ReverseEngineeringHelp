package com.ggupdater.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class JarUtils {

	public static HashMap<String, ClassNode> parseJar(JarFile jar) {

		HashMap<String, ClassNode> classes = new HashMap<>();

		try {

			Enumeration<?> enumeration = jar.entries();
			while (enumeration.hasMoreElements()) {

				JarEntry entry = (JarEntry) enumeration.nextElement();
				if (entry.getName().endsWith(".class")) {

					ClassReader classReader = new ClassReader(jar.getInputStream(entry));
					ClassNode classNode = new ClassNode();
					classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
					classes.put(classNode.name, classNode);

				}

			}
			jar.close();
			return classes;

		} catch (Exception e) {
			return null;
		}

	}

}
