package wumpus;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;


public class Window extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public Window() throws IOException {
		setLayout(new BorderLayout());
		RightPanel rPanel = new RightPanel();
		DrawingPanel d=new DrawingPanel(rPanel);
		rPanel.setDrawingPanel(d);
		
		add(d,BorderLayout.CENTER);
		
		add(rPanel,BorderLayout.EAST);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	

}
