package com.zhc.example.machinelearning;

import java.io.Serializable;

public class JavaLabeledDocument implements Serializable {
    private long id;
    private String text;
    private double label;

    public JavaLabeledDocument(long id, String text, double label) {
        this.id = id;
        this.text = text;
        this.label = label;
    }

    public long getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public double getLabel() {
        return this.label;
    }
}