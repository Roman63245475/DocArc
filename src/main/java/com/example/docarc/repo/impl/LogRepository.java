package com.example.docarc.repo.impl;

import com.example.docarc.bll.LogService;
import com.example.docarc.repo.repositories.ILogRepository;

import java.util.List;

public class LogRepository implements ILogRepository {

    @Override
    public boolean saveAppLogs(List<String> logs) {
        return false;
    }

    @Override
    public boolean saveErrorLogs(List<String> logs) {
        return false;
    }
}
