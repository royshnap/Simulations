package logic.rule.action.impl;

import logic.rule.action.api.ActionType;

public enum ReplaceType {
    SCRATCH, DERIVED;

    public static ReplaceType getReplaceType(String type){

        switch (type){
            case "scratch":
                return SCRATCH;
            case "derived":
                return DERIVED;

        }
        throw new IllegalArgumentException("The type " + type + " is not exists");
    }
}
