package org.yawlfoundation.yawl.analyser.util.alloy;

import org.yawlfoundation.yawl.analyser.util.alloy.descriptors.TranslationGenerator;
import org.yawlfoundation.yawl.elements.YNet;

public class AlloyAnalyzer {

    /**
     * checks whether two or more OR-joins are in a cycle, resulting in a potential
     * vicious circle.
     */
    public String analyzeWithAlloy(YNet net) {
        try {
            TranslationGenerator translationGenerator = new TranslationGenerator(net, null);
            String alloyDescription = translationGenerator.generateDescription();
            String alloyPredDescription = translationGenerator.generatePredDescription();
            return alloyDescription + alloyPredDescription;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String analyzeWithAlloyWithOrJoinReplacement(YNet net, String orJoinTaskName) {
        try {
            TranslationGenerator translationGenerator = new TranslationGenerator(net, orJoinTaskName);
            String alloyDescription = translationGenerator.generateDescription();
            String alloyPredDescription = translationGenerator.generatePredDescription();
            return alloyDescription + alloyPredDescription;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
