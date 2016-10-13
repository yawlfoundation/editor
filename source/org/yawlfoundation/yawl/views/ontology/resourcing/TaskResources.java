package org.yawlfoundation.yawl.views.ontology.resourcing;

import java.util.Collections;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 23/09/2016
 */
public class TaskResources {

    private Set<String> _participants;
    private Set<String> _roles;
    private String _familiarTask;
    private String _fourEyesTask;

    public TaskResources() {
        _participants = Collections.emptySet();
        _roles = Collections.emptySet();
    }

    public Set<String> getParticipants() {
        return _participants;
    }

    public void setParticipants(Set<String> participants) {
        _participants = participants;
    }

    public Set<String> getRoles() {
        return _roles;
    }

    public void setRoles(Set<String> roles) {
        _roles = roles;
    }

    public String getFamiliarTask() {
        return _familiarTask;
    }

    public void setFamiliarTask(String familiarTask) {
        _familiarTask = familiarTask;
    }

    public String getFourEyesTask() {
        return _fourEyesTask;
    }

    public void setFourEyesTask(String fourEyesTask) {
        _fourEyesTask = fourEyesTask;
    }

}
