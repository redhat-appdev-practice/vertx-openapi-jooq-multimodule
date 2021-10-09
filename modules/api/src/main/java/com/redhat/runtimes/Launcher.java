package com.redhat.runtimes;

public class Launcher extends io.vertx.core.Launcher {
	
	
	/**
	 * Main entry point.
	 *
	 * @param args the user command line arguments.
	 */
	public static void main(String[] args) {
		System.setProperty("vertx.setClassPathResolvingEnabled", "true");
		System.setProperty("vertx.setFileCachingEnabled", "true");
		
		new io.vertx.core.Launcher().dispatch(args);
	}
}
