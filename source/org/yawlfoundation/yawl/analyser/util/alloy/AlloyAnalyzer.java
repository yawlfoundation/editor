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
            return new TranslationGenerator(net).generate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
