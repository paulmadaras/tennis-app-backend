package com.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExportStrategyFactory {

    @Autowired
    private CSVExportStrategy csvExportStrategy;

    @Autowired
    private TXTExportStrategy txtExportStrategy;

    public ExportStrategy getStrategy(String format) {
        if ("csv".equalsIgnoreCase(format)) {
            return csvExportStrategy;
        } else if ("txt".equalsIgnoreCase(format)) {
            return txtExportStrategy;
        } else {
            throw new IllegalArgumentException("Invalid export format: " + format);
        }
    }
}
