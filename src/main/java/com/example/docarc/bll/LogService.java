package com.example.docarc.bll;

import com.example.docarc.repo.impl.LogRepository;
import com.example.docarc.repo.repositories.ILogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LogService {
    private ILogRepository logRepository;
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    public LogService(){
        logRepository = new LogRepository();
    }
    public void sendLogs() {

        File dir = new File("logs/old_logs");
        File[] files = null;
        if (dir.exists() && dir.isDirectory()) {
            files = dir.listFiles();
        }
        if (files != null) {
            for (File file : files) {
                List<String> logs = new ArrayList<String>();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String strLine;
                    String fileName = file.getName();
                    //reading log file
                    while ((strLine = br.readLine()) != null)   {
                        logs.add(strLine);
                    }
                    boolean saved = false;
                    if (fileName.startsWith("app.")){
                        saved = logRepository.saveAppLogs(logs);
                    }
                    else if (fileName.startsWith("error.")) {
                        saved = logRepository.saveErrorLogs(logs);
                    }
                    if (saved) {
                        if (!file.delete()) {
                            file.deleteOnExit();
                            //logger.warn("Failed to delete a file: {}", fileName);
                        }
                    } else {
                        logger.warn("File not deleted because save failed {}", fileName);
                    }

                }
                catch (IOException e) {
                    //logger.error("Failed to process log file", e);
                    System.out.println("omg");
                }
            }
        }
    }
}
