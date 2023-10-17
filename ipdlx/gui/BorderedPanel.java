package ipdlx.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class BorderedPanel extends JPanel {
    
    public BorderedPanel(String title) {
	this(title, Color.gray);
    }

    public BorderedPanel(String title, Color borderColor) {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	Border tb = BorderFactory.
	    createTitledBorder(new LineBorder(borderColor, 1, true),
			       " " + title + " ");
	
	setBorder(new CompoundBorder(tb, new EmptyBorder(5, 5, 5, 5)));
    }
}


