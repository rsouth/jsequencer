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
import java.awt.event.ActionListener;
import java.io.File;

public class SeqStatusBar extends JPanel {

    private final JLabel filePathLabel = new JLabel("");

    private final JLabel fileName = new JLabel("");

    SeqStatusBar(ActionListener exportAsImage, ActionListener copyToClipboard) {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        JButton statusBarExportButton = new JButton("Export");
        statusBarExportButton.setHorizontalAlignment(SwingConstants.RIGHT);
        statusBarExportButton.addActionListener(exportAsImage);

        JButton statusBarCopyToClipboardButton = new JButton("Copy to Clipboard");
        statusBarCopyToClipboardButton.setHorizontalAlignment(SwingConstants.RIGHT);
        statusBarCopyToClipboardButton.addActionListener(copyToClipboard);

        rightPanel.add(statusBarExportButton);
        rightPanel.add(statusBarCopyToClipboardButton);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        filePathLabel.setHorizontalAlignment(SwingConstants.LEFT);
        filePathLabel.setVerticalAlignment(SwingConstants.CENTER);
        fileName.setFont(fileName.getFont().deriveFont(Font.BOLD));

        leftPanel.add(filePathLabel);
        leftPanel.add(fileName);

        this.add(leftPanel);
        this.add(Box.createHorizontalGlue());
        this.add(rightPanel);
    }

    void setFileName(File file) {
        SwingUtilities.invokeLater(() -> {
            if(file == null) {
                this.filePathLabel.setText("");
                this.fileName.setText("");

            } else {
                this.filePathLabel.setText(file.getParent() + File.separator);
                this.fileName.setText(file.getName());
            }
        });
    }

}
