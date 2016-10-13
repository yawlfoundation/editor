package org.yawlfoundation.yawl.views.ontology;


/**
 * @author Michael Adams
 *         - prototype by Gary Grossgarten (h-brs.de)
 *         <p>
 *         Test class (manually loads and saves files)
 */

public class Main {


    public static void main(String[] args) {
        try {
            SpecificationParser specificationParser = FileUtil.loadSpecification();
            if (specificationParser != null) {
                FileUtil.exportAsOwl(populateOntology(specificationParser));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static OntologyPopulator populateOntology(SpecificationParser specificationParser) {
        OntologyPopulator ontologyPopulator = new OntologyPopulator(
                "source/org/yawlfoundation/yawl/views/ontology/file/SpecificationOntology.owl");
        ontologyPopulator.addIndividuals(specificationParser);
        ontologyPopulator.addObjectProperties(specificationParser);
        return ontologyPopulator;
    }

}
