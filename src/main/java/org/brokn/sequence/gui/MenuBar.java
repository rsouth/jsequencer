/*
 *     Copyright (C) 2020 rsouth (https://github.com/rsouth)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.brokn.sequence.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static javax.swing.KeyStroke.getKeyStroke;

public class MenuBar extends JMenuBar {

    private final JMenu menuFile = new JMenu("File");
    private final JMenu menuDiagram = new JMenu("Diagram");
    private final JMenu menuHelp = new JMenu("Help");

    public static final String FILE_NEW = "New";
    public static final String FILE_OPEN = "Open";
    public static final String FILE_SAVE = "Save";
    public static final String FILE_SAVE_AS = "Save As...";

    public static final String DIAGRAM_ADD_TITLE = "Add Title";
    public static final String DIAGRAM_ADD_AUTHOR = "Add Author";
    public static final String DIAGRAM_ADD_DATE = "Add Date";
    public static final String DIAGRAM_COPY_TO_CLIPBOARD = "Copy to Clipboard";
    public static final String DIAGRAM_EXPORT_AS = "Export as...";

    public static final String HELP_GRAMMAR = "Grammar";
    public static final String HELP_ABOUT = "About";
    public static final String HELP_EXAMPLE_FILE = "Example File";

    public MenuBar(AbstractAction fileMenuListener, AbstractAction diagramMenuListener, AbstractAction helpMenuListener) {
        createMenuItems();
        attachMouseListeners(fileMenuListener, diagramMenuListener, helpMenuListener);
    }

    private void attachMouseListeners(AbstractAction fileMenuListener, AbstractAction diagramMenuListener, AbstractAction helpMenuListener) {
        for (Component menuComponent : this.menuFile.getMenuComponents()) {
            if( menuComponent instanceof JMenuItem) {
                ((JMenuItem) menuComponent).addActionListener(fileMenuListener);
            }
        }

        for (Component menuComponent : this.menuDiagram.getMenuComponents()) {
            if (menuComponent instanceof JMenuItem) {
                ((JMenuItem) menuComponent).addActionListener(diagramMenuListener);
            }
        }

        for (Component menuComponent : this.menuHelp.getMenuComponents()) {
            if (menuComponent instanceof JMenuItem) {
                ((JMenuItem) menuComponent).addActionListener(helpMenuListener);
            }
        }
    }

    private JMenuItem createMenuItem(String text) {
        return new JMenuItem(text);
    }

    private JMenuItem createMenuItem(String text, int mnemonic) {
        JMenuItem menuItem = createMenuItem(text);
        menuItem.setMnemonic(mnemonic);
        return menuItem;
    }

    private JMenuItem createMenuItem(String text, int mnemonic, KeyStroke accelerator) {
        JMenuItem menuItem = createMenuItem(text, mnemonic);
        menuItem.setAccelerator(accelerator);
        return menuItem;
    }

    private void createMenuItems() {
        // File
        KeyStroke keyStroke_Ctrl_N = getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
        KeyStroke keyStroke_Ctrl_O = getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
        KeyStroke keyStroke_Ctrl_S = getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
        menuFile.add(createMenuItem(FILE_NEW, KeyEvent.VK_N, keyStroke_Ctrl_N));
        menuFile.add(createMenuItem(FILE_OPEN, KeyEvent.VK_O, keyStroke_Ctrl_O));
        menuFile.add(createMenuItem(FILE_SAVE, KeyEvent.VK_S, keyStroke_Ctrl_S));
        menuFile.add(createMenuItem(FILE_SAVE_AS, KeyEvent.VK_A));
        menuFile.setMnemonic(KeyEvent.VK_F);
        this.add(menuFile);

        // Diagram
        KeyStroke keyStroke_Alt_T = getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK);
        KeyStroke keyStroke_Alt_A = getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK);
        KeyStroke keyStroke_Alt_D = getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK);
        KeyStroke keyStroke_Alt_C = getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK);
        KeyStroke keyStroke_Alt_E = getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK);
        menuDiagram.add(createMenuItem(DIAGRAM_ADD_TITLE, KeyEvent.VK_T, keyStroke_Alt_T));
        menuDiagram.add(createMenuItem(DIAGRAM_ADD_AUTHOR, KeyEvent.VK_A, keyStroke_Alt_A));
        menuDiagram.add(createMenuItem(DIAGRAM_ADD_DATE, KeyEvent.VK_D, keyStroke_Alt_D));
        menuDiagram.add(new JSeparator());
        menuDiagram.add(createMenuItem(DIAGRAM_COPY_TO_CLIPBOARD, KeyEvent.VK_C, keyStroke_Alt_C));
        menuDiagram.add(createMenuItem(DIAGRAM_EXPORT_AS, KeyEvent.VK_E, keyStroke_Alt_E));

        menuDiagram.setMnemonic(KeyEvent.VK_D);
        this.add(menuDiagram);

        // Tools (todo, not yet)

        // Help
        menuHelp.add(createMenuItem(HELP_GRAMMAR));
        menuHelp.add(createMenuItem(HELP_ABOUT));
        menuHelp.add(new JSeparator());
        menuHelp.add(createMenuItem(HELP_EXAMPLE_FILE));

        menuHelp.setMnemonic(KeyEvent.VK_H);
        this.add(menuHelp);
    }

}
