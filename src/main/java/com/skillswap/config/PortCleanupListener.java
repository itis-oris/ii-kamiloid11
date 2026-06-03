package com.skillswap.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
public class PortCleanupListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment env = event.getEnvironment();
        int port = env.getProperty("server.port", Integer.class, 8080);
        killProcessOnPort(port);
    }

    private void killProcessOnPort(int port) {
        String os = System.getProperty("os.name", "").toLowerCase();
        try {
            if (os.contains("win")) {
                killOnWindows(port);
            } else {
                killOnUnix(port);
            }
        } catch (Exception e) {
            log.debug("Port cleanup check for {} completed: {}", port, e.getMessage());
        }
    }

    private void killOnWindows(int port) throws Exception {
        Process process = Runtime.getRuntime().exec(
                new String[]{"cmd", "/c", "netstat -ano | findstr :" + port + " | findstr LISTENING"});
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 5) {
                    String pid = parts[parts.length - 1];
                    if (!"0".equals(pid)) {
                        log.warn("Port {} is occupied by PID {}. Killing it.", port, pid);
                        Runtime.getRuntime().exec(new String[]{"taskkill", "/F", "/PID", pid}).waitFor();
                        log.info("Killed PID {} on port {}", pid, port);
                    }
                }
            }
        }
    }

    private void killOnUnix(int port) throws Exception {
        Process process = Runtime.getRuntime().exec(
                new String[]{"sh", "-c", "lsof -ti:" + port});
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String pid = line.trim();
                if (!pid.isEmpty()) {
                    log.warn("Port {} is occupied by PID {}. Killing it.", port, pid);
                    Runtime.getRuntime().exec(new String[]{"kill", "-9", pid}).waitFor();
                    log.info("Killed PID {} on port {}", pid, port);
                }
            }
        }
    }
}
