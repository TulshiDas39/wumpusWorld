package wumpus;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.Timer;



public class DrawingPanel extends JPanel implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int board[][];
	int humanturnx[]=new int[100];
	int humanturny[]=new int[100];
	public boolean riskMook=false;
	public boolean editingMode = false;
	HashMap<HashSet<Integer>, Integer> map = new HashMap<>();
	public int x;
	public int y;
	Point previousManPositon = new Point(9, 0);
	Point manPosition = new Point();
	
	
	
	Color colors[]= {Color.LIGHT_GRAY,Color.BLUE,Color.RED,Color.magenta,Color.ORANGE};
	String[] imageNames = {"blank.PNG","man.png","gold.jpg","pit.jpg","bridge.jpg","stench.jpg","wumpus.gif","deadWumpus.jpg",
			"manGold.png","manBridge.png","manSmog.png","deadWumpusMan.png","attack.gif","goldPit.png",
			"goldBridge.png","goldSmog.png","bridgeSmog.png","GoldBridgeMan.png","fallingInPIT.gif",
			"goldBridgeStench.png","manBridgeSmog.png","manGoldBridgeSmogg.png","manGoldSmog.png","climbing.gif"}; 
	
	int turn; // turn=1 // turn=2 // turn= 3
	
	Timer timer;
	int sideLength = 70;
	int imageNumber = 24;
	final int man = 1,gold = 2,pit = 3, bridge=4,stench = 5,wumpus = 6, deadWumpus = 7,falling=18,attack=12,blank=0,climbing=23;
	boolean gameOver;
	RightPanel rPanel;
	Image image;
	private Image[] images = new Image[imageNumber];
	
	private Node[][] state = new Node[10][10];
	private int[][] agentState = new int[10][10];
	
	Agent agent;
	int cost = 0;
	final int moveCost = -1;
	final int goldCost = 1000;
	
	public DrawingPanel(RightPanel rPanel) {
		this.rPanel = rPanel;
		setBackground(Color.DARK_GRAY);
		setLayout(new GridLayout(10, 10));	
		
		init();
		setEntities();
		turn =1;		
		timer=new Timer(1000,this);
		MouseHandler handler = new MouseHandler();
		addMouseListener( handler );
		addMouseMotionListener(handler);
		makeAgent();
		
		setTimer();

		
	}
	
	private void setTimer() {
		timer= new Timer(1000, new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent e) {
		    	  startMove();	    	  
		      }
		    });
			/*timer.start();*/
	}

	private void makeAgent() {
		agent = new Agent(this,this.rPanel);
		agentState = agent.state;
	}

	public void startMove() {
		
		int status;
		
			status = agent.makeMove(manPosition);
			
			if(status == 0 || status ==2){
				System.out.println("status:"+status);
			}
			else{
				state[previousManPositon.x][previousManPositon.y].remove(man, map);
				state[manPosition.x][manPosition.y].add(man, map);
				previousManPositon.x = manPosition.x;
				previousManPositon.y = manPosition.y;
				
				rPanel.displayThinking(agent.thinking);
				agentState = agent.state;
				cost+=-1;
			}
			
			repaint();
			if(status ==100){
				timer.stop();
				JOptionPane.showMessageDialog(null, "Found Gold");
				cost+=goldCost;
			}
			
			rPanel.area.setText("Cumulative Cost:"+rPanel.area.getText()+cost);
		
	}

	public DrawingPanel() {
		
	}

	private void setEntities() {
		HashSet<Integer>taken = new HashSet<>();
		taken.add(90);
		taken.add(91);
		taken.add(80);
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				state[i][j]= new Node() ;
			}
		}
		
		state[9][0].imageIndex = man;
		Random rn = new Random();
		
		setWumpus(taken, rn);
		setPit(taken,rn);
		
		setGold(taken,rn);
		
	}
	
	public void restart(){
		setEntities();
		repaint();
	}

	private void setGold(HashSet<Integer> taken, Random rn) {
		
		int goldNum = rn.nextInt(100);
		int goldX = goldNum/10;
		int goldY = goldNum%10;
		HashSet<Integer> entity = state[goldX][goldY].getEntities();
		while(entity.contains(man) || entity.contains(wumpus) || entity.contains(deadWumpus) ){
			goldNum = rn.nextInt(100);
			goldX = goldNum/10;
			goldY = goldNum%10;
			entity = state[goldX][goldY].getEntities();
		}
		
		state[goldX][goldY].add(gold, map);
		
	}

	private void setPit(HashSet<Integer> taken, Random rn) {
		
		int pitNum = rn.nextInt(15)+1;
		
		int pitIndex;
		
		int pitX;
		int pitY;
		
		while(pitNum>0){
			pitIndex = rn.nextInt(100);
			
			while(taken.contains(pitIndex)){
				pitIndex = rn.nextInt(100);
			}
			pitX = pitIndex/10;
			pitY = pitIndex%10;
			
			
			state[pitX][pitY].add(pit, map);
			taken.add(pitIndex);
			if(validIndex(pitX-1, pitY)){
				state[pitX-1][pitY].add(bridge, map);
				taken.add((pitX-1)*10+pitY);
			}
			
			if(validIndex(pitX, pitY-1)){
				state[pitX][pitY-1].add(bridge, map);
				taken.add(pitX*10 +pitY-1);
			}
			
			if(validIndex(pitX+1, pitY)){
				state[pitX+1][pitY].add(bridge, map);
				taken.add((pitX+1)*10+pitY);
			}
			
			if(validIndex(pitX, pitY+1)){
				state[pitX][pitY+1].add(bridge, map);
				taken.add(pitX*10 + pitY+1);
			}
			
			pitNum--;
		}
		
	}

	private void setWumpus(HashSet<Integer> taken, Random rn) {
		int wumpusNum = rn.nextInt(100);
		
		while(taken.contains(wumpusNum)){
			wumpusNum = rn.nextInt(100);
		}
		int wumpusX = wumpusNum/10;
		int wumpusY = wumpusNum%10;
	
		state[wumpusX][wumpusY].add(wumpus, map);
		taken.add(wumpusNum);
		if(validIndex(wumpusX-1, wumpusY)){
			state[wumpusX-1][wumpusY].add(stench, map);
			taken.add((wumpusX-1)*10+wumpusY);
		}
		
		if(validIndex(wumpusX, wumpusY-1)){
			state[wumpusX][wumpusY-1].add(stench, map);
			taken.add(wumpusX*10 +wumpusY-1);
		}
		
		if(validIndex(wumpusX+1, wumpusY)){
			state[wumpusX+1][wumpusY].add(stench, map);
			taken.add((wumpusX+1)*10+wumpusY);
		}
		
		if(validIndex(wumpusX, wumpusY+1)){
			state[wumpusX][wumpusY+1].add(stench, map);
			taken.add(wumpusX*10 + wumpusY+1);
		}
	}
	
	
	private boolean validIndex(int x, int y){
		if(x>9 || x <0 || y>9 || y<0){
			 return false;
		}
		return true;
	}
	

	private void init() {
		
		try {
			makeImagePosition();
		} catch (MalformedURLException e1) {
			
			e1.printStackTrace();
		}
		
		
		HashSet<Integer>imageNo = new HashSet<>();
		for(int i=0;i<8;i++){
			imageNo.add(i);
			map.put(imageNo, i);
			imageNo = new HashSet<>();
		}
		imageNo.add(gold);
		imageNo.add(man);
		map.put(imageNo, 8);
		imageNo = new HashSet<>();
		
		imageNo.add(man);
		imageNo.add(bridge);
		map.put(imageNo, 9);
		imageNo = new HashSet<>();
		
		imageNo.add(man);
		imageNo.add(stench);
		map.put(imageNo, 10);
		imageNo = new HashSet<>();
		
		imageNo.add(deadWumpus);
		imageNo.add(man);
		map.put(imageNo, 11);
		imageNo = new HashSet<>();
		
		imageNo.add(12);
		map.put(imageNo, 12);
		imageNo = new HashSet<>();
		
		imageNo.add(gold);
		imageNo.add(pit);
		map.put(imageNo, 13);
		imageNo = new HashSet<>();
		imageNo.add(gold);
		imageNo.add(bridge);
		map.put(imageNo, 14);
		imageNo = new HashSet<>();
		imageNo.add(gold);
		imageNo.add(stench);
		map.put(imageNo, 15);
		
		imageNo = new HashSet<>();
		imageNo.add(bridge);
		imageNo.add(stench);
		map.put(imageNo, 16);
		
		imageNo = new HashSet<>();
		imageNo.add(gold);
		imageNo.add(bridge);
		imageNo.add(man);
		map.put(imageNo, 17);
		
		imageNo = new HashSet<>();
		imageNo.add(18);
		map.put(imageNo, 18);
		
		imageNo = new HashSet<>();
		imageNo.add(gold);
		imageNo.add(bridge);
		imageNo.add(stench);
		map.put(imageNo, 19);
		

		imageNo = new HashSet<>();
		imageNo.add(stench);
		imageNo.add(bridge);
		imageNo.add(man);
		map.put(imageNo, 20);
		
		imageNo = new HashSet<>();
		imageNo.add(stench);
		imageNo.add(bridge);
		imageNo.add(man);
		imageNo.add(gold);
		map.put(imageNo, 21);
		
		imageNo = new HashSet<>();
		imageNo.add(stench);
		imageNo.add(man);
		imageNo.add(gold);
		map.put(imageNo, 22);
		imageNo = new HashSet<>();
		imageNo.add(23);
		map.put(imageNo, 23);
		
	
	}
	
	

	
	private void display() {	
    	JFrame frame = new JFrame("Example");
    	
    	frame.setLayout(new GridLayout(5,1,0,2));
    	JButton jb1 = new JButton("Remove The entity");
    	jb1.setFocusPainted(false);
    	jb1.setBackground(Color.gray);
    	jb1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				frame.dispose();
				System.out.println("in popup:"+x+" "+ y);
				removeEntity(y/sideLength,x/sideLength);
			}
		});

    	
    	frame.add(jb1);
    	
    	JButton jb2 = new JButton("Set Pit");
    	jb2.setBackground(Color.gray);
    	jb2.setFocusPainted(false);
    	jb2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.out.println("in popup:"+x+" "+ y);
				addPit(y/sideLength, x/sideLength);
			
			}
		});
    	frame.add(jb2);
    	
    	JButton jb3 = new JButton("Set Dead Wumpus");
    	jb3.setBackground(Color.gray);
    	jb3.setFocusPainted(false);
    	jb3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.out.println("in popup:"+x+" "+ y);
				addDeadWumpus(y/sideLength, x/sideLength);
			
				
			}
		});
    	
    	frame.add(jb3);
    	
    	JButton jb4 = new JButton("Set Wumpus");
    	jb4.setBackground(Color.gray);
    	jb4.setFocusPainted(false);
    	jb4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.out.println("in popup:"+x+" "+ y);
				addWumpus(y/sideLength, x/sideLength);
				
			}
		});
    	frame.add(jb4);
    	
    	JButton jb5 = new JButton("Set Gold");
    	jb5.setBackground(Color.gray);
    	jb5.setFocusPainted(false);
    	jb5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				addGold(y/sideLength, x/sideLength);
				
			}
		});
    	frame.add(jb5);
    	
    	
    	KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addVetoableChangeListener("focusedWindow",
            new VetoableChangeListener() {
              private boolean gained = false;

              @Override
              public void vetoableChange(PropertyChangeEvent evt)
                  throws PropertyVetoException {
                if (evt.getNewValue() == frame) {
                  gained = true;
                }
                if (gained && evt.getNewValue() != frame) {
                  frame.dispose();
                  
                }
              }
            });
    	
    	
    	int y1=y;
    	if(y1>500)y1=500;
    	frame.setBounds(x,y1,200,200);
    	frame.setUndecorated(true);
    	frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
    	frame.setVisible(true);
    }
	
	public void addPit(int x, int y){
		
		if(hasSameAdjacentEntity(x,y,pit) || !state[x][y].getEntities().isEmpty() || !editableIndex(x,y)){
			JOptionPane.showMessageDialog(null, "Inconsistent Command","Error",JOptionPane.ERROR_MESSAGE);
		}
		else{
			state[x][y].add(pit, map);
			
			addEntityArount(x, y, bridge);
			
		}
		
		repaint();
		
	}
	
	private boolean editableIndex(int x, int y) {
		if(x == 9 && y==0) return false;
		if(x == 9 && y==1) return false;
		if(x == 8 && y==0) return false;
		return true;
	}

	private void addEntityArount(int x, int y, int entity){
		if(validIndex(x-1, y)) state[x-1][y].add(entity, map);
		if(validIndex(x, y-1)) state[x][y-1].add(entity, map);
		if(validIndex(x+1, y)) state[x+1][y].add(entity, map);
		if(validIndex(x, y+1)) state[x][y+1].add(entity, map);
	}
	
	private boolean hasSameAdjacentEntity(int x, int y, int entity){
		if(validIndex(x-1, y)){
			if(state[x-1][y].getEntities().contains(entity)) return true;
		}

		if(validIndex(x, y-1)){
			if(state[x][y-1].getEntities().contains(entity)) return true;
		}

		if(validIndex(x+1, y)){
			if(state[x+1][y].getEntities().contains(entity)) return true;
		}
		if(validIndex(x, y+1)){
			if(state[x][y+1].getEntities().contains(entity)) return true;
		}
		
		return false;

	}
	
	public void addDeadWumpus(int x, int y){
		
		if(hasSameAdjacentEntity(x,y,deadWumpus) || !state[x][y].getEntities().isEmpty() || !editableIndex(x, y)){
			JOptionPane.showMessageDialog(null, "Inconsistent Command","Error",JOptionPane.ERROR_MESSAGE);
		}
		else{
			state[x][y].add(deadWumpus, map);
			
			addEntityArount(x, y, stench);
			
		}
		
		repaint();
		
		
	}
	
	public void addWumpus(int x, int y){
		
		if(hasSameAdjacentEntity(x,y,deadWumpus) || !state[x][y].getEntities().isEmpty() || !editableIndex(x, y)){
			JOptionPane.showMessageDialog(null, "Inconsistent Command","Error",JOptionPane.ERROR_MESSAGE);
		}
		else{
			state[x][y].add(wumpus, map);
			
			addEntityArount(x, y, stench);
			
		}
		
		repaint();
		
	}
	
	
	
	public void addGold(int x, int y){
		HashSet<Integer> entity = state[x][y].getEntities();
		if(entity.contains(this.wumpus ) || entity.contains(this.deadWumpus) || (x == 9 && y == 0) ){
			JOptionPane.showMessageDialog(null, "Conflict");
		}
		else {
			state[x][y].add(gold, this.map);
		}
		
	}
	
	
	private void removeEntity(int x,int y){
		System.out.println("In removeEntity:"+x+" "+y);
		
		HashSet<Integer> entity = state[x][y].getEntities();
		if(entity.contains(gold)){
			state[x][y].remove(gold, map);
		}
		else if(entity.contains(pit)){
			if(validIndex(x-1, y) && ! adjacentToSameTypeOfEntity(x-1,y,x,y,pit)) state[x-1][y].remove(bridge, map);
			if(validIndex(x, y-1)  && ! adjacentToSameTypeOfEntity(x,y-1,x,y,pit)) state[x][y-1].remove(bridge, map);
			if(validIndex(x+1, y)  && ! adjacentToSameTypeOfEntity(x+1,y,x,y,pit)) state[x+1][y].remove(bridge, map);
			if(validIndex(x, y+1)  && ! adjacentToSameTypeOfEntity(x,y+1,x,y,pit)) state[x][y+1].remove(bridge, map);
			state[x][y].remove(pit, map);
			
		}
		else if(entity.contains(wumpus)){
			if(validIndex(x-1, y) && ! adjacentToSameTypeOfEntity(x-1,y,x,y,wumpus)) state[x-1][y].remove(stench, map);
			if(validIndex(x, y-1)  && ! adjacentToSameTypeOfEntity(x,y-1,x,y,wumpus)) state[x][y-1].remove(stench, map);
			if(validIndex(x+1, y) && ! adjacentToSameTypeOfEntity(x+1,y,x,y,wumpus)) state[x+1][y].remove(stench, map);
			if(validIndex(x, y+1) && ! adjacentToSameTypeOfEntity(x,y+1,x,y,wumpus)) state[x][y+1].remove(stench, map);
			state[x][y].remove(wumpus, map);
		}
		
		else if(entity.contains(deadWumpus)){
			if(validIndex(x-1, y) && ! adjacentToSameTypeOfEntity(x-1,y,x,y,deadWumpus)) state[x-1][y].remove(stench, map);
			if(validIndex(x, y-1)  && ! adjacentToSameTypeOfEntity(x,y-1,x,y,deadWumpus)) state[x][y-1].remove(stench, map);
			if(validIndex(x+1, y)  && ! adjacentToSameTypeOfEntity(x+1,y,x,y,deadWumpus)) state[x+1][y].remove(stench, map);
			if(validIndex(x, y+1) && ! adjacentToSameTypeOfEntity(x,y+1,x,y,deadWumpus)) state[x][y+1].remove(stench, map);
			state[x][y].remove(deadWumpus, map);
		}
		else {
			JOptionPane.showMessageDialog(null, "Inconsistent command","Error",JOptionPane.ERROR_MESSAGE);
		}
		
		
		repaint();
	}
	
	
	
	
	private boolean adjacentToSameTypeOfEntity(int x1, int y1, int x2, int y2, int entity) {
		if(validIndex(x1-1, y1)){
			if(x1 -1 != x2 || y1!=y2){
				if(state[x1-1][y1].getEntities().contains(entity)) return true;
			}
		}
		if(validIndex(x1, y1-1)){
			if(x1 != x2 || y1 -1 !=y2){
				if(state[x1][y1-1].getEntities().contains(entity)) return true;
			}
		}
		if(validIndex(x1+1, y1)){
			if(x1 +1 != x2 || y1!=y2){
				if(state[x1+1][y1].getEntities().contains(entity)) return true;
			}
		}
		if(validIndex(x1, y1+1)){
			if(x1 != x2 || y1+1 !=y2){
				if(state[x1][y1+1].getEntities().contains(entity)) return true;
			}
		}

		return false;
	}

	public DrawingPanel getThisObject(){
		return this;
	}
	
	private class MouseHandler implements MouseListener,MouseMotionListener{

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(editingMode){
				x = e.getX();
				y = e.getY();
				System.out.println(x+" "+y);
				if(e.getButton() == MouseEvent.BUTTON3){
					EventQueue.invokeLater(getThisObject()::display);
				}
			}
			
			timer.start();
		}

		

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
	
		for(int i=0;i<10*sideLength;i+=sideLength) {
			for(int j=0;j<10*sideLength;j+=sideLength) {
				g.drawImage(images[state[j/sideLength][i/sideLength].imageIndex], i, j, sideLength-2, sideLength-2, this);
				if(agentState[j/sideLength][i/sideLength] != -1){
					g.setFont(new Font("Sherif",Font.BOLD,20));
					g.drawString("Safe", i, j+(sideLength/2));
				}
			}
		}
	}

	private void makeImagePosition() throws MalformedURLException {
		String path = System.getProperty("user.dir");
		URL url;
		
		for(int i=0;i<imageNumber;i++){
					url = new URL("file:///"+path+"/images/"+imageNames[i]);
					images[i] = new ImageIcon(url).getImage();
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}

	
	public HashSet<Integer> getEntity(int x, int y){
		System.out.println("before return:"+this.state[x][y].getEntities().size());
		return this.state[x][y].getEntities();
	}

	public void pauseTimer() {
		if(timer.isRunning()) timer.stop();
		
	}

	public void startTimer() {
		if(!timer.isRunning())timer.start();
		
	}
	
}
