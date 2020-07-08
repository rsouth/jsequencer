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
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class SeqStatusBar extends JPanel {

    public SeqStatusBar(JPanel contentPane, ActionListener exportAsImage, ActionListener copyToClipboard) {
        this.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        this.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.setPreferredSize(new Dimension(contentPane.getWidth(), 25));

        JButton statusBarExportButton = new JButton("Export");
        statusBarExportButton.setHorizontalAlignment(SwingConstants.RIGHT);
        statusBarExportButton.addActionListener(exportAsImage);
        this.add(statusBarExportButton);

        JButton statusBarCopyToClipboardButton = new JButton("Copy to Clipboard");
        statusBarCopyToClipboardButton.setHorizontalAlignment(SwingConstants.RIGHT);
        statusBarCopyToClipboardButton.addActionListener(copyToClipboard);
        this.add(statusBarCopyToClipboardButton);
    }

}
