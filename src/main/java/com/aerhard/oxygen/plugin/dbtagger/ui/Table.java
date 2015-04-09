package com.aerhard.oxygen.plugin.dbtagger.ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Table extends JTable {

    public Table () {
        setDefaultFocusTraversal();
    }

    /**
     * Sets default focus traversal rules: tab and shift tab should select new siblings rather than other parts of the table.
     */
    private void setDefaultFocusTraversal() {
        Set<AWTKeyStroke> forward = new HashSet<AWTKeyStroke>(
                getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                forward);
        Set<AWTKeyStroke> backward = new HashSet<AWTKeyStroke>(
                getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                backward);
    }

}
