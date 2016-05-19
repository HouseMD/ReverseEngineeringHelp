package com.ggupdater.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.jar.JarFile;

import org.objectweb.asm.tree.ClassNode;

import com.ggupdater.analysers.AbstractAnalyser;
import com.ggupdater.analysers.CharacterAnalyser;
import com.ggupdater.analysers.ClientAnalyser;
import com.ggupdater.analysers.DualNodeAnalyser;
import com.ggupdater.analysers.EntityAnalyser;
import com.ggupdater.analysers.LinkedListAnalyser;
import com.ggupdater.analysers.ModelAnalyser;
import com.ggupdater.analysers.NodeAnalyser;
import com.ggupdater.analysers.StringStorageAnalyser;
import com.ggupdater.hooks.ClassHook;
import com.ggupdater.hooks.FieldHook;
import com.ggupdater.hooks.Hook;
import com.ggupdater.utils.JarUtils;

public class GGUpdater {

	public static LinkedHashMap<String, Hook> HOOKS = new LinkedHashMap<>();
	public static HashMap<String, ClassNode> CLASSES = new HashMap<>();
	public ArrayList<AbstractAnalyser> ANALYSERS = new ArrayList<>();

	public static void main(String args[]) {

		System.out.println("HouseMD's Updater v1.0");
		new GGUpdater();

	}

	public GGUpdater() {
		JarFile jar;
		try {
			jar = new JarFile("revisions" + File.separator + "gamepack88.jar");
			CLASSES = JarUtils.parseJar(jar);
			Log("[REV " + ClientAnalyser.getRevision(CLASSES.get("client")) + "]");
			Log(CLASSES.values().size() + " Classes parsed.");
			loadAnalysers();
			runAnalysers();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String hookName : HOOKS.keySet()) {

			Hook hook = HOOKS.get(hookName);
			if (hook instanceof FieldHook) {

				FieldHook fh = (FieldHook) hook;
				if (fh.getMultiplier() != 0)
					System.out.println(String.format("[%s] %s %s %s", "F", fh.getHookName(), fh.getReturnFieldName(), fh.getMultiplier()));
				else
					System.out.println(String.format("[%s] %s %s", "F", fh.getHookName(), fh.getReturnFieldName()));

			} else if (hook instanceof ClassHook) {

				ClassHook ch = (ClassHook) hook;
				System.out.println(String.format("[%s] %s %s", "C", ch.getHookName(), ch.getClazzName()));

			}

		}

	}

	private void loadAnalysers() {

		ANALYSERS.add(new NodeAnalyser());
		ANALYSERS.add(new DualNodeAnalyser());
		ANALYSERS.add(new LinkedListAnalyser());
		ANALYSERS.add(new EntityAnalyser());
		ANALYSERS.add(new CharacterAnalyser());
		ANALYSERS.add(new ModelAnalyser());
		ANALYSERS.add(new StringStorageAnalyser());
		ANALYSERS.add(new ClientAnalyser());

	}

	private void runAnalysers() {

		for (AbstractAnalyser analyser : ANALYSERS) {

			for (ClassNode node : CLASSES.values()) {

				analyser.run(node);

			}

		}

	}

	public static ClassHook getClassHook(String name) {

		for (String key : HOOKS.keySet()) {

			if (key.equals(name))
				return (ClassHook) HOOKS.get(key);

		}
		return null;

	}

	private void Log(String message) {

		System.out.println("[+] " + message);

	}

}
