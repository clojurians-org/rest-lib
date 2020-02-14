package com.bumblebee.acquisition.core.model;

import java.io.InputStream;
import java.io.InputStreamReader;

public class FileModel {
    private InputStreamReader inputStreamReader;
    private String fileName;
    private String filePath;


    public InputStreamReader getInputStreamReader() {
        return inputStreamReader;
    }

    public void setInputStreamReader(InputStreamReader inputStreamReader) {
        this.inputStreamReader = inputStreamReader;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
