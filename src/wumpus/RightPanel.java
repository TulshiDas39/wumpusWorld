package wumpus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class RightPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	Color colors[]= {Color.LIGHT_GRAY,Color.BLUE,Color.RED,Color.magenta,Color.ORANGE};
	String position[] = {"1st","2nd","3rd","4th","5th","6th","7th","8th","9th","10th"};
	
	int turn;
	JLabel labelGIF;
	
	boolean gameOver;
	

	JLabel status;
	DrawingPanel drawingPanel;
	JTextArea area;
	
	public RightPanel() throws MalformedURLException {
		
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(660, getHeight()));
		setLayout(null);
		
		makeHeading();
		
		makeEditingCheckbox();
		
		makeRiskCheckbox();
		makeRestartButton();
		makeMindArea();
		makeStartButton();
		makePauseButton();
		
		status = new JLabel("");
		status.setFont(new Font("Sherif",Font.BOLD,25));
		status.setHorizontalAlignment(getWidth());
		status.setForeground(Color.PINK);
		status.setFont(new Font("Sherif",Font.PLAIN,20));
		
		
		add(status);
		
	}

	private void makePauseButton() {

		
		JButton pauseButton = new JButton("Pause");
		pauseButton.setBounds(130,210,80,50);
		pauseButton.setBackground(Color.black);
		pauseButton.setForeground(Color.red);
		pauseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				drawingPanel.pauseTimer();		
			}
		});
		add(pauseButton);
		
	
		
	}

	private void makeMindArea() {
		JLabel label = new JLabel("Agent's Mind");
		label.setBounds(280,230,200,40);
		label.setFont(new Font("Sherif",Font.PLAIN,30));
		label.setForeground(Color.GREEN);
		add(label);
		area = new JTextArea();
		area.setBackground(Color.LIGHT_GRAY);
		area.setEditable(false);
	
		JScrollPane jScrolPane = new JScrollPane(area);
		jScrolPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jScrolPane.setBounds(30,290,600,400);
		JScrollBar vertical = jScrolPane.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
		add(jScrolPane);
		
	}
	
	private void makeStartButton() {
		
		JButton startButton = new JButton("Start");
		startButton.setBounds(10,210,100,50);
		startButton.setBackground(Color.black);
		startButton.setForeground(Color.red);
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				drawingPanel.startTimer();		
			}
		});
		add(startButton);
		
	}

	public void displayThinking(String str){
		area.setText(str);
	}

	private void makeRestartButton() {
		
		JButton startButton = new JButton("Reload Environment");
		startButton.setBounds(300,110,200,50);
		startButton.setBackground(Color.black);
		startButton.setForeground(Color.red);
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				drawingPanel.restart();		
			}
		});
		add(startButton);
		
	}

	private void makeRiskCheckbox() {
		JCheckBox riskMode = new JCheckBox("Risk Mode:");
		riskMode.setBounds(10,130,200,50);
		riskMode.setBackground(Color.black);
		riskMode.setForeground(Color.red);
		riskMode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 drawingPanel.riskMook = !(drawingPanel.riskMook);				
			}
		});
		add(riskMode);
	}

	private void makeEditingCheckbox() {
		JCheckBox editingMode = new JCheckBox("Editing Mode:");
		editingMode.setBounds(10,100,200,50);
		editingMode.setBackground(Color.black);
		editingMode.setForeground(Color.green);
		editingMode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 drawingPanel.editingMode = !(drawingPanel.editingMode);				
			}
		});
		add(editingMode);
	}

	private void makeHeading() {
		JLabel heading;
		heading = new JLabel("Wumpus World");
		heading.setBounds(160, 3, 400, 100);
		heading.setForeground(Color.YELLOW);
		heading.setFont(new Font("Algerian",Font.BOLD,40));
		add(heading);
	}
	
	public void setDrawingPanel(DrawingPanel dp){
		this.drawingPanel = dp;
	}
	
	public void addWinGIF() throws MalformedURLException {
		String  path= System.getProperty("user.dir");
		URL url = new URL("file:///"+path+"/congrats.gif");
        Icon icon = new ImageIcon(url);
        labelGIF = new JLabel(icon);
        add(labelGIF);
        
	}	
	
	public void addLoseGIF() throws MalformedURLException{
		
	    String  path= System.getProperty("user.dir");
		URL url = new URL("file:///"+path+"/game_over.gif");
        Icon icon = new ImageIcon(url);
        labelGIF = new JLabel(icon);
        labelGIF.setPreferredSize(new Dimension(getWidth(), 50));
        add(labelGIF);
	}
	
	public void viewWinner(String status){
		this.status.setText(status);
		this.status.setFont(new Font("Sherif",Font.BOLD,25));
		this.status.setForeground(Color.PINK);
	}
	
}
		
		
		
	
	
	
