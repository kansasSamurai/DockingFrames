/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.*;
import javax.swing.border.Border;

/**
 * @author Janni Kovacs
 */
public class EclipseBorder implements Border {
	private Color lineColor = SystemColor.controlShadow;
	
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;
//		Polygon corner = new Polygon(new int[]{0, 1, 1, 4, 5, 6}, new int[]{6, 5, 4, 1, 1, 0}, 6);
		g.setColor(lineColor);
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2d.setStroke(new BasicStroke(1f));
//		g.drawPolygon(corner);
		// top left corner
		g.drawLine(4, 1, 5, 1);
		g.drawLine(3, 2, 3, 2);
		g.drawLine(2, 3, 2, 3);
		g.drawLine(1, 4, 1, 5);
		// top right corner
		g.drawLine(width - 5, 1, width - 6, 1);
		g.drawLine(width - 4, 2, width - 4, 2);
		g.drawLine(width - 3, 3, width - 3, 3);
		g.drawLine(width - 2, 4, width - 2, 5);
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.drawLine(0, 6, 0, height - 1);
		g.drawLine(0, height - 1, width - 1, height - 1);
		g.drawLine(width - 1, 6, width - 1, height - 1);
		g.drawLine(6, 0, width - 7, 0);
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(1, 1, 1, 1);
	}

	public boolean isBorderOpaque() {
		return false;
	}
}