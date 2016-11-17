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


    public void invert() {
        String temp = subject;
        subject = object;
        object = temp;
    }


    public String toCSV() {
        return getSubject() + "," + getPredicate() + "," + getObject();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple triple = (Triple) o;

        return equals(subject, triple.subject) &&
                equals(predicate, triple.predicate) &&
                equals(object, triple.object);

    }

    private boolean equals(String a, String b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (predicate != null ? predicate.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        return result;
    }


    private String removePrefix(String element, String prefix) {
        return element.replace(prefix, "");
    }

}
