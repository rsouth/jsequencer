package org.brokn.sequence.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TabDocumentTitle extends JPanel {

    private final JLabel titleLabel;

    private final JButton closeButton;

    public TabDocumentTitle() {
        this.titleLabel = new JLabel("");
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        this.closeButton = new JButton("X");
        this.closeButton.setBorderPainted(false);
        this.closeButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.closeButton.setContentAreaFilled(false);
        this.closeButton.setHideActionText(true);
        this.closeButton.setHorizontalTextPosition(0);
        this.closeButton.setIcon(new ImageIcon(getClass().getResource("/icons/close-tab.png")));
        rightPanel.add(closeButton);

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        this.setOpaque(false);

        this.add(leftPanel);
        this.add(rightPanel);
    }

    public void addCloseButtonListener(ActionListener listener) {
        this.closeButton.addActionListener(listener);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(MouseEvent.BUTTON2 == e.getButton()) {
                    closeButton.doClick();
                }
            }
        });
    }

    public void setTitle(String title) {
        this.titleLabel.setText(title);
        this.doLayout();
    }

}