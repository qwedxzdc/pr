package com.project.twitter.validator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateResult {
    boolean isValid;
    Map<String, String> messages = new HashMap<>();
}