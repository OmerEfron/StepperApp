package DTO.FlowExecutionData.impl;

import java.io.Serializable;

/**
 * holds an input/output data from a flow that has been executed.
 */
public class IOData implements Serializable {

    private final boolean isOutput;
    private final String name;
    private final String type;
    private final String content;
    private final String necessity;
    private final String userString;
    private final Object value;
    private final String FullQualifiedName;



    public IOData(boolean isOutput, String name, String userString, String type, String content, String necessity, Object value, String fullQualifiedName) {
        this.isOutput = isOutput;
        this.name = name;
        this.userString = userString;
        this.type = type;
        this.content = content;
        this.necessity = necessity;
        this.value=value;
        FullQualifiedName = fullQualifiedName;
    }

    public String getFullQualifiedName() {
        return FullQualifiedName;
    }

    public <T> T getDataValue (Class<T> exceptedDataType) {
        return exceptedDataType.cast(value);
    }
    public boolean isOutput() {
        return isOutput;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getNecessity() {
        return necessity;
    }

    public String getUserString() {
        return userString;
    }
}
