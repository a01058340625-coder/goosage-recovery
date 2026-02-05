package com.goosage.dto.study;

public class PredictionDto {

    private String level;
    private String horizon;
    private String basis;

    public PredictionDto() {}

    public PredictionDto(String level, String horizon, String basis) {
        this.level = level;
        this.horizon = horizon;
        this.basis = basis;
    }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getHorizon() { return horizon; }
    public void setHorizon(String horizon) { this.horizon = horizon; }

    public String getBasis() { return basis; }
    public void setBasis(String basis) { this.basis = basis; }
}
