package com.elsevier.entellect.service.codesloader;

import java.util.Set;

public interface IClassificationCodesLoader {

    void onStartup();

    void onSchedule();

    void reloadClassificationCodes();

    Set<String> getClassificationCodes();

}
