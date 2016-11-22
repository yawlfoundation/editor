package org.yawlfoundation.yawl.views.query;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class ColorTextPane extends JTextPane {

    private final Map<Color, AttributeSet> _attrMap;


    public ColorTextPane() {
        super();
        _attrMap = new HashMap<Color, AttributeSet>();
        addColor(Color.BLACK);      // a default
    }


    public void addColor(Color color) {
        StyleContext context = StyleContext.getDefaultStyleContext();
        _attrMap.put(color, context.addAttribute(SimpleAttributeSet.EMPTY,
                            StyleConstants.Foreground, color));
    }


    public void append(String text) {
        append(text, Color.BLACK);
    }

    public void append(String text, Color color) {
        if (color == null) color = Color.BLACK;
        AttributeSet attributeSet = _attrMap.get(color);
        if (attributeSet == null) attributeSet = _attrMap.get(Color.BLACK);

        setCaretPosition(getDocument().getLength());
        setCharacterAttributes(attributeSet, false);
        replaceSelection(text);
    }

}
