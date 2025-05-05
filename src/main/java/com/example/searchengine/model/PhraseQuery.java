package com.example.searchengine.model;


public record PhraseQuery(String phrase1, String phrase2, String operator) {

    @Override
    public String toString() {
        return "PhraseQuery{" +
                "phrase1='" + phrase1 + '\'' +
                ", phrase2='" + phrase2 + '\'' +
                ", operator='" + operator + '\'' +
                '}';
    }
}