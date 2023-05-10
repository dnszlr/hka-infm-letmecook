package com.zeller.letmecook.model;

import java.util.List;

public record Fridge(List<Food> content, float throwAwayAmount) {}
