package com.wmh.robotframework.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReadSign {
    private String content;
    private long pos;
}
