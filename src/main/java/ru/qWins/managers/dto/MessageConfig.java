package ru.qWins.managers.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageConfig {
    private List<String> text;
    private List<String> hover;
    private boolean enabled;
    private long interval;
    private String sound;
    private String clickUrl;
    private boolean clickEnabled;
}