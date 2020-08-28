package com.tpssoft.hham.service;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchConstraints {
    private List<SearchConstraint> constraints = new ArrayList<>();
}
