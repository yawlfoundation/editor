package org.yawlfoundation.yawl.views.resource;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.resourcing.resource.Role;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * @author Michael Adams
 * @date 13/7/20
 */
public class PersistedRoleColors {

    private static final String PROPS_FILENAME = "lib/rolecolors.properties";
    private static final String COMMENT = "Role-Color Pairs";

    public Map<String, Color> load(Set<String> filterBy) {
        Properties props = new Properties();
        try {
            props.load(new FileReader(getFilePath()));
            return loadIntoMap(props, filterBy);
        }
        catch (IOException ioe) {
            return new HashMap<>();
        }
    }


    public boolean save(Map<String, Color> map) {
        Map<String, Color> colorMap = load(Collections.emptySet());
        colorMap.putAll(map);
        Properties props = new Properties();
        for (String key : colorMap.keySet()) {
            Color color = colorMap.get(key);
            String roleName = getRoleNameByID(key);
            props.put(roleName, encode(color));
        }

        try {
            Writer w = new FileWriter(getFilePath());
            props.store(w, COMMENT);
            return true;
        }
        catch (IOException ioe) {
            return false;
        }
    }


    private  Map<String, Color> loadIntoMap(Properties props, Set<String> filterBy) {
        Map<String, Color> map = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            String roleID = getRoleIDByName(key);
            if (roleID != null && (filterBy.isEmpty() || filterBy.contains(roleID))) {
                String value = props.getProperty(key);
                map.put(roleID, decode(value));
            }
        }
        return map;
    }


    private String encode(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }


    private Color decode(String value) {
        try {
           return Color.decode(value);
        }
        catch (NumberFormatException nfe) {
            return new Color((int)(Math.random() * 0x1000000));
        }
    }


    private File getFilePath() {
        File jarFile = new File(YAWLEditor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return new File(jarFile.getParentFile(), PROPS_FILENAME);
    }


    private String getRoleIDByName(String name) {
        for (Role role : SpecificationModel.getHandler().getResourceHandler().getRoles()) {
             if (name.equals(role.getName())) {
                 return role.getID();
             }
        }
        return null;
    }

    private String getRoleNameByID(String id) {
        for (Role role : SpecificationModel.getHandler().getResourceHandler().getRoles()) {
             if (id.equals(role.getID())) {
                 return role.getName();
             }
        }
        return "unknown";
    }

    
}
