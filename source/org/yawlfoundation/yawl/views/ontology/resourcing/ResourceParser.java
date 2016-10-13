package org.yawlfoundation.yawl.views.ontology.resourcing;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 23/09/2016
 */
public class ResourceParser {


    public TaskResources parse(YTask task) {
        Element resElement = task.getResourcingSpecs();
        if (resElement != null) {
            return parseOffer(resElement);
        }
        return null;
    }

    private TaskResources parseOffer(Element e) {
        Element eOffer = e.getChild("offer", e.getNamespace());

        // if offer is not system-initiated, there's no more to do
        if (isSystemInitiated(eOffer)) {
            TaskResources taskResources = parseDistributionSet(eOffer);
            if (taskResources != null) {
                taskResources.setFamiliarTask(parseFamiliarTask(eOffer));
            }
            return taskResources;
        }
        return null;
    }


    private boolean isSystemInitiated(Element e) {
        String initiator = e.getAttributeValue("initiator");
        return initiator != null && initiator.equals("system");
    }


    private TaskResources parseDistributionSet(Element e) {
        Namespace nsYawl = e.getNamespace();
        Element eDistSet = e.getChild("distributionSet", nsYawl);
        TaskResources taskResources = null;
        if (eDistSet != null) {
            taskResources = parseInitialSet(eDistSet);
            taskResources.setFourEyesTask(parseConstraints(eDistSet));
        }
        return taskResources;
    }


    private TaskResources parseInitialSet(Element e) {
        Element eInitialSet = e.getChild("initialSet", e.getNamespace());
        TaskResources taskResources = null;
        if (eInitialSet != null) {
            taskResources = new TaskResources();
            taskResources.setParticipants(parse(eInitialSet, "participant"));
            taskResources.setRoles(parse(eInitialSet, "role"));
        }
        return taskResources;
    }


    private Set<String> parse(Element e, String name) {
        Set<String> participants = new HashSet<String>();
        if (e != null) {
            for (Element eItem : e.getChildren(name, e.getNamespace())) {
                String pid = eItem.getText();
                if (pid.contains(",")) {
                    participants.addAll(splitCSV(pid));
                }
                else participants.add(pid);
            }
        }
        return participants;
    }


    public String parseConstraints(Element e) {
        Namespace ns = e.getNamespace();
        Element eConstraints = e.getChild("constraints", ns);
        if (eConstraints != null) {
            Element eConstraint = eConstraints.getChild("constraint", ns);
            if (eConstraint != null) {
                String constraintName = eConstraint.getChildText("name", ns);
                if (constraintName != null && constraintName.equals("SeparationOfDuties")) {
                    Element eParams = eConstraint.getChild("params", ns);
                    if (eParams != null) {
                        Element eParam = eParams.getChild("param", ns);
                        if (eParam != null) {
                            return eParam.getChildText("value", ns);
                        }
                    }
                }
            }
        }
        return null;
    }


    private String parseFamiliarTask(Element e) {
        Element eFamTask = e.getChild("familiarParticipant", e.getNamespace());
        return eFamTask != null ? eFamTask.getAttributeValue("taskID") : null;
    }


    private List<String> splitCSV(String idList) {
        String[] ids = idList.split(",");
        for (int i = 0; i < ids.length; i++) {
            ids[i] = ids[i].trim();
        }
        return Arrays.asList(ids);
    }

}
