package ru.javawebinar.topjava.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserMeal {
    private final LocalDateTime dateTime;
    private final String description;
    private final int calories;

}
