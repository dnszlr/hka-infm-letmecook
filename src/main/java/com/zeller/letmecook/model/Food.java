package com.zeller.letmecook.model;

import java.time.LocalDate;

public record Food(float amount, Unit unit, float price, LocalDate purchase, LocalDate expiration) {}
