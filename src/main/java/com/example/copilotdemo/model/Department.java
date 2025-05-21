package com.example.copilotdemo.model;

public enum Department {
    DARK_FORCES("Dark Forces"),
    EVIL_PLANNING("Evil Planning"),
    MINION_MANAGEMENT("Minion Management"),
    CORRUPTION("Corruption"),
    CONQUEST("Conquest"),
    DARK_MAGIC("Dark Magic"),
    OPERATIONS("Operations");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
