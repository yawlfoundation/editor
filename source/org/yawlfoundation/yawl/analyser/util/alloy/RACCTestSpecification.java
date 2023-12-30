package org.yawlfoundation.yawl.analyser.util.alloy;

public class RACCTestSpecification {
    private String sourceTaskName;
    private String destinationTaskName;
    private String testDescription;
    private String testAssignments;

    public RACCTestSpecification(String taskName, String destinationTaskName, String testDescription) {
        this.sourceTaskName = taskName;
        this.destinationTaskName = destinationTaskName;
        this.testDescription = testDescription;
    }

    public String getSourceTaskName() {
        return sourceTaskName;
    }

    public void setSourceTaskName(String sourceTaskName) {
        this.sourceTaskName = sourceTaskName;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public String getTestAssignments() {
        return testAssignments;
    }

    public void setTestAssignments(String testAssignments) {
        this.testAssignments = testAssignments;
    }

    public String getDestinationTaskName() {
        return destinationTaskName;
    }

    public void setDestinationTaskName(String destinationTaskName) {
        this.destinationTaskName = destinationTaskName;
    }
}
