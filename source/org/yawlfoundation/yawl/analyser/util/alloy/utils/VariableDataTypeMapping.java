package org.yawlfoundation.yawl.analyser.util.alloy.utils;

import java.util.HashMap;

public class VariableDataTypeMapping {
    public static HashMap<String, String> mapping;
    static {
        mapping = new HashMap<>();
        mapping.put("string", "String");
        mapping.put("int", "Integer");
        mapping.put("integer", "Integer");
    }
}
