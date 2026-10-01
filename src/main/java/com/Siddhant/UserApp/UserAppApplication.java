package com.Siddhant.UserApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
public class UserAppApplication {
	public static void main(String[] args) {
		freePort8080IfJava();
		SpringApplication.run(UserAppApplication.class, args);
	}

	private static void freePort8080IfJava() {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				// Find PID listening on 8080
				Process netstatProcess = Runtime.getRuntime().exec("cmd.exe /c netstat -ano | find \"LISTENING\" | find \":8080\"");
				BufferedReader reader = new BufferedReader(new InputStreamReader(netstatProcess.getInputStream()));
				String line = reader.readLine();
				if (line != null && !line.trim().isEmpty()) {
					String[] parts = line.trim().split("\\s+");
					String pid = parts[parts.length - 1];
					
					// Check if process is java.exe
					Process tasklistProcess = Runtime.getRuntime().exec("cmd.exe /c tasklist /FI \"PID eq " + pid + "\" /NH");
					BufferedReader tasklistReader = new BufferedReader(new InputStreamReader(tasklistProcess.getInputStream()));
					String taskLine;
					boolean isJava = false;
					while ((taskLine = tasklistReader.readLine()) != null) {
						if (taskLine.toLowerCase().contains("java.exe") || taskLine.toLowerCase().contains("javaw.exe")) {
							isJava = true;
							break;
						}
					}
					
					if (isJava) {
						System.out.println("PortGuard: Found stale Java process (PID: " + pid + ") running on port 8080. Terminating it...");
						Runtime.getRuntime().exec("cmd.exe /c taskkill /F /PID " + pid).waitFor();
						System.out.println("PortGuard: Process terminated successfully.");
						Thread.sleep(1000); // Give OS a moment to free the port
					} else {
						System.out.println("PortGuard: Process on 8080 (PID: " + pid + ") is not a Java process. Skipping termination.");
					}
				}
			}
		} catch (Exception e) {
			System.err.println("PortGuard: Failed to check/free port 8080 - " + e.getMessage());
		}
	}
}
