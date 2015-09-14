package org.yawlfoundation.yawl.editor.ui.properties.data.validation;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

import java.util.List;
import java.util.Random;

/**
 * @author Michael Adams
 * @date 11/09/15
 */
public class RegexValueGenerator {

    private final Random random;
    private final Automaton automaton;

    public RegexValueGenerator(String pattern) {
        automaton = new RegExp(pattern, RegExp.NONE).toAutomaton();  // no optional flags
        random = new Random();
    }

    public String generate() {
        StringBuilder builder = new StringBuilder();
        generatePatternMatch(builder, automaton.getInitialState());
        return builder.toString();
    }


    private void generatePatternMatch(StringBuilder builder, State state) {
        List<Transition> transitions = state.getSortedTransitions(true);
        if (transitions.size() == 0) {
            return;
        }
        int options = state.isAccept() ? transitions.size() : transitions.size() - 1;
        int option = getRandomInt(0, options, random);
        if (state.isAccept() && option == 0) {          // 0 is considered stop
            return;
        }

        // Move to next transition
        Transition transition = transitions.get(option - (state.isAccept() ? 1 : 0));
        char c = (char) getRandomInt(transition.getMin(), transition.getMax(), random);
        builder.append(c);
        generatePatternMatch(builder, transition.getDest());
    }


    private int getRandomInt(int min, int max, Random random) {
        int dif = max - min;
        float number = random.nextFloat();              // 0 <= number < 1
        return min + Math.round(number * dif);
    }

}
