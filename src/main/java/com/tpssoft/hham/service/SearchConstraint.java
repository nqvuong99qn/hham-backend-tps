package com.tpssoft.hham.service;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.tpssoft.hham.service.SearchConstraint.MatchMode.EQUALS;

/**
 * A class representing constraints to filter out search results.
 */
@Data
@NoArgsConstructor
public class SearchConstraint {
    /**
     * Match mode determine how the search value should be matched
     */
    public enum MatchMode {
        /**
         * Match if the search value is a substring of the value being tested
         */
        SUBSTRING,
        /**
         * Match using `equals()`
         */
        EQUALS,
        /**
         * Match using `==` comparison
         */
        IDENTITY,
    }

    /**
     * Name of the field to apply this constraint. This information is not used while matching,
     * it is declared here to allow caller determine which field to apply the constraint.
     */
    private String fieldName;
    /**
     * The value to match against, the type of this value depends on match mode:
     * - `SUBSTRING`: This value is of type `String` or a type with `toString()` properly implemented
     * - `EXACT`: This value can be of any type that has `equals()` properly implemented
     */
    private Object searchValue;
    /**
     * A value indicating how the search value should be used while matching
     */
    private MatchMode matchMode;

    /**
     * Shorthand constructor for `EXACT` match mode.
     *
     * @param fieldName   The field name where this constraint is applied
     * @param searchValue The value to match
     */
    public SearchConstraint(String fieldName, Object searchValue) {
        this(fieldName, searchValue, EQUALS);
    }
/////////
////
    /**
     * Construct a new search constraint.
     *
     * @param fieldName   The field name where this constraint is applied
     * @param searchValue The value to match
     * @param matchMode   The matching strategy to use
     */
    public SearchConstraint(String fieldName, Object searchValue, MatchMode matchMode) {
        if (matchMode == null) {
            throw new IllegalArgumentException("Match mode can't be null");
        }
        if (matchMode != MatchMode.IDENTITY && searchValue == null) {
            throw new IllegalArgumentException("Search value can't be null");
        }
        // Field name is not checked because it is not used inside this class
        this.fieldName = fieldName;
        this.searchValue = searchValue;
        this.matchMode = matchMode;
    }

    /**
     * Perform a test to determine whether the provided value satisfies the constraint expressed
     * by this object.
     *
     * This method relies on `equals()` (for `EXACT` mode) and `toString()` (for `SUBSTRING` mode),
     * so don't use it with
     *
     * @param value The value to test
     *
     * @return `true` if the value provided satisfies this constraint, `false` otherwise
     */
    public boolean matches(Object value) {
        switch (matchMode) {
            case EQUALS:
                return searchValue.equals(value);
            case IDENTITY:
                return searchValue == value;
            case SUBSTRING:
                return value.toString().contains(searchValue.toString());
            default:
                throw new IllegalStateException("Unimplemented match mode: " + matchMode);
        }
    }
}


