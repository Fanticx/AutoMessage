package ru.qWins.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessageConfig {
    private List<String> text;
    private List<String> hover;
    private boolean enabled;
    private long interval;
    private String sound;
    private String clickUrl;
    private boolean clickEnabled;
}