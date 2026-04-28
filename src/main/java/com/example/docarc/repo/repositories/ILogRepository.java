package com.example.docarc.repo.repositories;

import java.util.List;

public interface ILogRepository {

    boolean saveAppLogs(List<String> logs);
    boolean saveErrorLogs(List<String> logs);
}
