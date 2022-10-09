package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsfilteredByCycles = filteredByCycles(meals, LocalTime.of(7, 0),
                LocalTime.of(13, 0), 2000);
        mealsfilteredByCycles.forEach(System.out::println);

        List<UserMealWithExcess> mealsfilteredByStreams = filteredByStreams(meals, LocalTime.of(7, 0),
                LocalTime.of(12, 0), 2000);
        mealsfilteredByStreams.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime,
                                                            LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> summaryCaloriesByDate = new HashMap<>();
        for (UserMeal userMeal : meals) {
            LocalDate userMealDate = toLocalDate(userMeal);
            summaryCaloriesByDate.merge(userMealDate, userMeal.getCalories(), Integer::sum);
            //Integer thisDateCalories = caloriesByDate.getOrDefault(userMealDate, 0);
            //caloriesByDate.put(userMealDate, Integer.sum(thisDateCalories, userMeal.getCalories()));
        }
        List<UserMealWithExcess> userMealExcesses = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalTime userMealTime = toLocalTime(userMeal);
            LocalDate userMealDate = toLocalDate(userMeal);
            if (TimeUtil.isBetweenHalfOpen(userMealTime, startTime, endTime)) {
                UserMealWithExcess userMealWithExcess = mapEntityToDto(userMeal, summaryCaloriesByDate.get(userMealDate) > caloriesPerDay);
                userMealExcesses.add(userMealWithExcess);
            }
        }
        return userMealExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime,
                                                             LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> summaryCaloriesByDate = meals.stream()
                .collect(Collectors.toMap(UserMealsUtil::toLocalDate,
                        UserMeal::getCalories, Integer::sum));
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(toLocalTime(userMeal), startTime, endTime))
                .map(userMeal -> mapEntityToDto(userMeal, summaryCaloriesByDate.get(toLocalDate(userMeal)) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static UserMealWithExcess mapEntityToDto(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(),
                userMeal.getCalories(), excess);

    }

    private static LocalTime toLocalTime(UserMeal userMeal) {
        return userMeal.getDateTime().toLocalTime();
    }

    private static LocalDate toLocalDate(UserMeal userMeal) {
        return userMeal.getDateTime().toLocalDate();
    }
}
