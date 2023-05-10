package com.zeller.letmecook.model;

import java.util.List;

public record Recipe(List<Food> ingredient, String description) {
}
