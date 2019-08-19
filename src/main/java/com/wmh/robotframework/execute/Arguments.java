package com.wmh.robotframework.execute;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Arguments {

    private final List<String> arguments = new ArrayList<String>();


    public void addFileToArguments(File file, String flag) {
        if (isFileValid(file)) {
            String path = !file.getName().toUpperCase().equals("NONE") ? String.format("\"%s\"", file.getPath()) : file.getName();
            add(flag, path);
        }
    }

    protected boolean isFileValid(File file) {
        return file != null && file.getPath() != null && !file.getPath().equals("");
    }

    public void addNonEmptyStringToArguments(String variableToAdd, String flag) {
        if (!StringUtils.isEmpty(variableToAdd)) {
            addStringToArguments(variableToAdd, flag);
        }
    }

    public void addFlagToArguments(boolean flag, String argument) {
        if (flag == true) {
            add(argument);
        }
    }

    public void addStringToArguments(String variableToAdd, String flag) {
        add(flag, variableToAdd);
    }

    public void addListToArguments(String variablesToAdd, String flag) {
        if (variablesToAdd == null) {
            return;
        }
        addListToArguments(new ArrayList<String>(Arrays.asList(StringUtils.split(variablesToAdd, ","))), flag);
    }

    public void addListToArguments(List<String> variablesToAdd, String flag) {
        if (variablesToAdd == null) {
            return;
        }
        for (String variableToAdd : variablesToAdd) {
            if (!StringUtils.isEmpty(variableToAdd)) {
                add(flag, variableToAdd);
            }
        }
    }

    public void addFileListToArguments(List<File> variablesToAdd, String flag) {
        if (variablesToAdd == null) {
            return;
        }
        for (File variableToAdd : variablesToAdd) {
            addFileToArguments(variableToAdd, flag);
        }
    }

    public void add(String... values) {
        for (String value : values)
            arguments.add(value);
    }

    public String[] toArray() {
        return arguments.toArray(new String[arguments.size()]);
    }

}
