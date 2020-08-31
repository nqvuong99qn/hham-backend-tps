package com.tpssoft.hham.service;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.tpssoft.hham.service.FindConstraint.MatchNode.EQUALS;
@Data
@NoArgsConstructor
public class FindConstraint {

    // using constraints to determine search results

    public enum MatchNode{
        STRING,

        EQUALS,

        IDENTITY
    }

    private String fieldName;

    private Object searchValue;

    private MatchNode matchNode;



    public FindConstraint(String fieldName, Object searchValue){
        this(fieldName, searchValue, EQUALS);
    }

    public FindConstraint(String fieldName, Object searchValue, MatchNode matchNode){
            if(matchNode == null){
                throw new IllegalArgumentException("Match node can't be null");
            }
            if(matchNode != MatchNode.IDENTITY && searchValue == null){
                throw new IllegalArgumentException("Search Value can't be null");
            }
            this.fieldName = fieldName;
            this.searchValue = searchValue;
            this.matchNode = matchNode;

    }

    public boolean matches(Object value){
            switch (matchNode){
                case EQUALS:
                    return searchValue.equals(value);
                case IDENTITY:
                    return searchValue == value;
                case STRING:
                    return value.toString().contains(searchValue.toString());
                default:
                    throw  new IllegalStateException("Unimplemented match node" + matchNode);
            }
    }
}
