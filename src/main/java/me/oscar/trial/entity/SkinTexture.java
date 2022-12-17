package me.oscar.trial.entity;

public class SkinTexture {

    private String value;
    private String signature;

    public SkinTexture(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public SkinTexture() {

    }

    public String getValue() {
        return this.value;
    }

    public String getSignature() {
        return this.signature;
    }
}
