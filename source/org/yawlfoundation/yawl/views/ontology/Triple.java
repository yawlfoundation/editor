package org.yawlfoundation.yawl.views.ontology;

/**
 * @author Michael Adams
 * @date 26/10/2016
 */
public class Triple {

    String subject;
    String predicate;
    String object;


    public Triple(String s, String p, String o) {
        subject = s;
        predicate = p;
        object = o;
    }

    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }


    public void removeSubjectPrefix(String prefix) {
        subject = removePrefix(subject, prefix);
    }


    public void removeObjectPrefix(String prefix) {
        object = removePrefix(object, prefix);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple triple = (Triple) o;

        return subject.equals(triple.subject) &&
                predicate.equals(triple.predicate) &&
                object.equals(triple.object);

    }

    @Override
    public int hashCode() {
        int result = subject.hashCode();
        result = 31 * result + predicate.hashCode();
        result = 31 * result + object.hashCode();
        return result;
    }


    private String removePrefix(String element, String prefix) {
        return element.replace(prefix, "");
    }

}
