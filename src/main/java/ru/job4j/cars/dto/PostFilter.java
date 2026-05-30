package ru.job4j.cars.dto;

import lombok.Getter;

@Getter
public enum PostFilter {

    ALL("all"),
    TODAY("today"),
    WITH_PHOTO("with-photo");

    private final String value;

    PostFilter(String value) {
        this.value = value;
    }

    public static PostFilter from(String value) {
        for (PostFilter filter : PostFilter.values()) {
            if (filter.value.equals(value)) {
                return filter;
            }
        }
        return ALL;
    }
}
