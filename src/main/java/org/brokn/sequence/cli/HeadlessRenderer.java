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

package org.brokn.sequence.cli;

import org.brokn.sequence.lexer.Lexer;
import org.brokn.sequence.rendering.Canvas;
import org.brokn.sequence.rendering.RenderableDiagram;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.brokn.sequence.gui.DialogUtils.exportAsImage;

public class HeadlessRenderer {

    void draw(HeadlessCli.CliValidationResult cliValidationResult) {
        // Read in .seq file
        String path = cliValidationResult.getCmd().getOptionValue("i");
        List<String> lines = new ArrayList<>();
        try {
            lines.addAll(Files.readAllLines(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create parser etc
        Lexer lexer = new Lexer();
        final RenderableDiagram parse = lexer.parse(String.join("\n", lines));

        // rendering env
        // todo change rendering to not require JFrame/JPanel; create Graphics2D from BufferedImage instead?
        JFrame frame = new JFrame("");
        Canvas canvas = new Canvas();

        frame.setVisible(true);
        frame.setContentPane(canvas);

        frame.setPreferredSize(parse.computeDiagramSize(canvas.getGraphics(), false));
        canvas.setPreferredSize(parse.computeDiagramSize(canvas.getGraphics(), false));
        frame.pack();

        canvas.updateModel(parse);
        parse.draw(canvas.getGraphics());

        // sort out output file
        String outputFileValue = cliValidationResult.getCmd().getOptionValue("o");
        File outFile = Paths.get(outputFileValue).toFile();

        exportAsImage(outFile, canvas);
    }

}
