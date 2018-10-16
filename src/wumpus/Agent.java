package wumpus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Agent {

	final int man = 1,gold = 2,pit = 3, bridge=4,stench = 5,wumpus = 6, deadWumpus = 7,falling=18,attack=12,blank=0,climbing=23;
	final int bridgeAndStench = 111;
	final int unknown =-1,ok = -2,notPit = -3,notWumpus = -6,notDeadWumpus = -7,mayPit = -53,mayWumpus = -56, mayDeadWumpus = -57;
	protected int[][] state = new int[10][10];
	private ArrayList<Statement> knowledge = new ArrayList<Statement>();
	public Point currentPosition = new Point();
	private DrawingPanel dPanel;
	private boolean back=false;
	private Point nextMove;
	private HashMap<Integer, Integer> notPossiblity = new HashMap<>();
	private HashMap<Integer, Integer> possiblity = new HashMap<>();
	private HashSet<Integer> safeEntity = new  HashSet<>();
	private HashMap<Integer, String> meaning = new HashMap<>();
	private int moveNum = 0;
	private ArrayList<Point> moves = new ArrayList<>();
	public String thinking="";
	private HashMap<Point, Boolean> visited = new HashMap<Point, Boolean>();
	
	public Agent(DrawingPanel dPanel, RightPanel rPanel) {
		this.dPanel = dPanel;
		init();
		currentPosition.setLocation(9, 0);
		state[9][0] = man;
		state[9][1] = ok;
		state[8][0] = ok;
		nextMove = new Point(9, 1);
		visited.put( new Point(currentPosition.x,currentPosition.y), true);
		moves.add(new Point(9,0));
		moves.add(new Point(9,1));
	}
	private void init() {
		
		for(int i=0;i<10;i++){
			for(int j =0;j<10;j++){
				state[i][j]= unknown;
			}
		}		
		setPossibilityMap();		
		setEntityName();
		
	}
	public Point previousMove(){
		return currentPosition;
	}
	private void setPossibilityMap() {
		notPossiblity.put(bridge, notWumpus);
		notPossiblity.put(stench, notPit);
		possiblity.put(bridge, pit);
		possiblity.put(stench, wumpus);
		
		safeEntity.add(bridge);
		safeEntity.add(stench);
		safeEntity.add(blank);
	}
	private void setEntityName() {
		meaning.put(gold,"gold");
		meaning.put(pit, "pit");
		meaning.put(bridge, "bridge");
		meaning.put(wumpus, "wumpus");
		meaning.put(blank, "blank");
		meaning.put(stench, "stench");
		meaning.put(deadWumpus, "dead Wumpus");
		meaning.put(unknown, "unknown");
		meaning.put(notPit, "not pit");
		meaning.put(notWumpus, "not wumpus");
		meaning.put(notDeadWumpus, "not dead wumpus");
	}
	
	public int makeMove(Point manPosition){
		
		moveNum++;
		
		currentPosition.x = nextMove.x;
		currentPosition.y = nextMove.y;
		manPosition.x = currentPosition.x;
		manPosition.y = currentPosition.y;
		
		visited.put(new Point(currentPosition.x,currentPosition.y),true);
		if(currentPosition.x == 9 && currentPosition.y == 0) {
		}
		
		System.out.println(moveNum+":Move  in ("+currentPosition.x+","+currentPosition.y+")");
		thinking += moveNum+":Move  in ("+currentPosition.x+","+currentPosition.y+")\n";
		System.out.println("before update:");
		displayState();
		
		
		if(back){
				setNextMove(currentPosition.x, currentPosition.y);
		}
		else{					
				HashSet<Integer> entities = dPanel.getEntity(currentPosition.x, currentPosition.y);
				System.out.println("entities:"+entities.size());
				if(entities.contains(gold)){
					thinking+="Yaa Found Gold!!\n";
					
					return 100;
				}
				else if(entities.isEmpty()){
					System.out.println("There is blank");
					thinking += "There is blank\n";
					updateKnowledgeForBlank();
				}
				else if(entities.contains(bridge) && entities.contains(stench)){
					System.out.println("There is bridge and stench");
					thinking += "There is bridge and stench";
					updateKnowledge(bridgeAndStench);
				}
				else if(entities.contains(bridge)){
					System.out.println("There is bridge");
					thinking += "There is bridge\n";
					updateKnowledge(bridge);
					
				}
				else if(entities.contains(stench)){
					System.out.println("There is stench");
					thinking+= "There is stench\n";
					updateKnowledge(stench);
				}
		}
		
		System.out.println("After update:");
		displayState();
		return setNextMove(currentPosition.x,currentPosition.y);				
	}
	private int setNextMove(int x, int y) {
		
		int [][]around = getAroundIndex(x,y);
		displayArray(around);
		for(int i=0;i<around.length;i++){
			if(validIndex(around[i][0], around[i][1])){
				if(state[around[i][0]][around[i][1]] != unknown){
					if(!visited.containsKey(new Point(around[i][0],around[i][1]))){
						nextMove.x = around[i][0];
						nextMove.y = around[i][1];
						
						moves.add(new Point(nextMove.x,nextMove.y));
						back =false;
						return 1;
					}
				}
			}
		}
		
		
		if(moves.size()==1)return 0;
		moves.remove(moves.size()-1);
		Point p = moves.get(moves.size()-1);
		nextMove.x =  p.x;
		nextMove.y = p.y;
		back = true;
		return 1;
		
		
	}
	private void displayArray(int[][] around) {
		System.out.println("Displaying Array");
		for(int i=0;i<around.length;i++){
			for(int j=0;j<around[i].length;j++){
					System.out.println("["+i+","+j+"]="+around[i][j]);
					if(visited.get(new Point(around[i][0],around[i][1])) != null){
						System.out.println("not null");
						if(visited.get(new Point(around[i][0],around[i][1])) == true) System.out.println("visited");
						else System.out.println("Not visited");
					}
					
			}
		}
		
	}
	private void displayState() {
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				if(state[i][j] != unknown){
					System.out.println("["+i+","+j+"]="+state[i][j]);
				}
			}
		}
		
	}
	private void updateKnowledge(int entity) {
		HashSet<Literal>newLiterals = new HashSet<>();
		int possibleEntity;
		
		if(notPossiblity.containsKey(entity)){
			possibleEntity = possiblity.get(entity);

			if(resolution(notPossiblity.get(entity),currentPosition.x+1,currentPosition.y)){
				include(new Literal(possibleEntity, currentPosition.x+1,currentPosition.y), newLiterals);
				
			}
		
			if(resolution(notPossiblity.get(entity),currentPosition.x,currentPosition.y+1)){
				include(new Literal(possibleEntity, currentPosition.x,currentPosition.y+1), newLiterals);
				
			}
			
			if(resolution(notPossiblity.get(entity),currentPosition.x-1,currentPosition.y)){
				include(new Literal(possibleEntity, currentPosition.x-1,currentPosition.y), newLiterals);
				
			}
			
			if(resolution(notPossiblity.get(entity),currentPosition.x,currentPosition.y-1)){
				include(new Literal(possibleEntity, currentPosition.x,currentPosition.y-1), newLiterals);
	
				
			}	
						
			Statement newStatement = new Statement(newLiterals);
			System.out.println(describe(newStatement));
			thinking+= describe(newStatement)+"\n";
			knowledge.add(newStatement);
		}
		
		else if(entity == blank){
			updateKnowledgeForBlank();
		}
		
		else if(entity == bridgeAndStench)updateKnowledgeForBridgeAndWumpus();	
				
	}
	private void updateKnowledgeForBridgeAndWumpus() {
		int x = currentPosition.x;
		int y = currentPosition.y;
		int [][]arr = getAroundIndex(x, y);
		HashSet<Literal>pitLiterals = new HashSet<>();
		HashSet<Literal>wumpusLiterals = new HashSet<>();
		for(int i=0;i<arr.length;i++){
			resolutionForBoth(arr[i][0],arr[i][1],pitLiterals,wumpusLiterals);
		}
		
		if(!pitLiterals.isEmpty()){
			Statement st = new Statement(pitLiterals);
			knowledge.add(st);
			updateState(st);
			System.out.println(describe(st));
			thinking+= describe(st)+"\n";
		}
		if(!wumpusLiterals.isEmpty()){
			Statement st1 = new Statement(wumpusLiterals);
			knowledge.add(st1);
			updateState(st1);
			System.out.println(describe(st1));
			thinking+= describe(st1)+"\n";
		}
		
		
	}
	private void updateState(Statement st) {
		
		Iterator<Literal> it = st.literals.iterator();
		
		if(st.literals.size()==1){
			Literal ent = it.next();
			if(ent.entity == this.pit){
				state[ent.position.x][ent.position.y] = this.pit;
			}
			else if(ent.entity == this.wumpus){
				state[ent.position.x][ent.position.y] = this.wumpus;
			}
		}
		
	}
	private void resolutionForBoth(int x, int y, HashSet<Literal> pitLiterals,HashSet<Literal> wumpusLiterals) {
		Literal pitLt = new Literal(this.pit, x, y);
		Literal wumpusLt = new Literal(this.wumpus, x, y);
		if(state[x][y] == unknown){
			Statement notPitSt = makeAtomicStatement(-this.pit, x, y);
			Statement notWumpusSt = makeAtomicStatement(-this.wumpus, x, y);
				if(!knowledge.contains(notPitSt)){
					pitLiterals.add(pitLt);
				}
				if(!knowledge.contains(notWumpusSt)){
					wumpusLiterals.add(wumpusLt);
				}
		}
	}
	
	private void updateKnowledgeForBlank() {
		int x = currentPosition.x;
		int y = currentPosition.y;
		int [][]arr = getAroundIndex(x, y);
		Statement notWumpusSt;
		Statement notPitS;
		for(int i=0;i<arr.length;i++){
			if(state[arr[i][0]][arr[i][1]] == unknown){
				notWumpusSt = makeAtomicStatement(notWumpus,arr[i][0],arr[i][1]);
				notPitS = makeAtomicStatement(notPit,arr[i][0],arr[i][1]);
				state[arr[i][0]][arr[i][1]] = ok;
				if(!knowledge.contains(notWumpusSt)) knowledge.add(notWumpusSt);
				if(!knowledge.contains(notPitS)) knowledge.add(notPitS);
				System.out.print("OK("+arr[i][0]+","+arr[i][1]+")");
				thinking+= "OK("+arr[i][0]+","+arr[i][1]+")";
			}
			System.out.println();
			thinking+="\n";
		}		
		
	}
	private boolean resolution(int entity, int x, int y) {
		if(validIndex(x,y)){
			if(state[x][y] == unknown){
				Literal lt = new Literal(entity,x,y);
				 inference(lt);
				 return true;
				
			}
		}
		return false;
	}
	private void inference(Literal entity) {
		Statement st;

		
		Literal oppEntity = oppositeLiteral(entity);
		for(int i=0;i<knowledge.size();i++){
			st = knowledge.get(i);
			if(st.literals.contains(oppEntity)){
				String s1 = describe(st)+" and "+describe(oppEntity)+"=";
				st.literals.remove(oppEntity);
				s1+= describe(st);
				System.out.println(s1);
				thinking+= s1+"\n";
				if(st.literals.size() ==1 ){
						Literal en = st.literals.iterator().next();
						state[en.position.x][en.position.y] = en.entity;
						defineSafeAround(en.position.x, en.position.y);
				}
			}	
		}		
	}
	private void include(Literal entity, HashSet<Literal> newLiterals) {
		int x = entity.position.x;
		int y = entity.position.y;
		Statement opSt = makeAtomicStatement(-entity.entity, entity.position.x, entity.position.y);
		if(knowledge.contains(opSt)){
			state[x][y] = ok;
		}
		else{
			newLiterals.add(entity);
		}
		
	}
	
	private void defineSafeAround(int x, int y) {
		int indexes[][] = getAroundIndex(x,y);
		Statement notWumpus;
		Statement notPit;
		
		for(int i=0;i<indexes.length;i++){
			if(validIndex(indexes[i][0], indexes[i][1])){
				notWumpus = makeAtomicStatement(this.notWumpus, indexes[i][0], indexes[i][1]);
				knowledge.add(notWumpus);
				notPit = makeAtomicStatement(this.notPit,indexes[i][0], indexes[i][1]);
				knowledge.add(notPit);
				state[indexes[i][0]][indexes[i][1]] = ok;
			}
		}		
		
	}
	
	
	private int[][] getAroundIndex(int x,int y) {
		int x1=0;
		int [][]arr = new int[4][2];
		if(validIndex(x+1, y)){
			arr[x1][0] = x+1;
			arr[x1][1] = y;
			x1++;
		}
		if(validIndex(x, y+1)){
			arr[x1][0] = x;
			arr[x1][1] = y+1;
			x1++;
		}
		if(validIndex(x-1, y)){
			arr[x1][0] = x-1;
			arr[x1][1] = y;
			x1++;
		}
		if(validIndex(x, y-1)){
			arr[x1][0] = x;
			arr[x1][1] = y-1;
			x1++;
		}
		
		int [][] newArr = new int[x1][2];
		for(int i=0;i<x1;i++){
			newArr[i][0]= arr[i][0];
			newArr[i][1] = arr[i][1];
		}
		return newArr;
	}
	private Statement makeAtomicStatement(int entity, int x, int y) {
		Literal lt = new Literal(entity, x, y);
		HashSet<Literal>lts = new HashSet<>();
		lts.add(lt);
		Statement st = new Statement(lts);
		return st;
	}
	private Literal oppositeLiteral(Literal lt){
		Literal opLit = new Literal(lt.entity, lt.position.x, lt.position.y);
		opLit.entity *=-1;
		return opLit;
	}
	
	private String describe(Statement st){
		String s="";
		
		int i=0;
		
		for( Iterator<Literal> it = st.literals.iterator();it.hasNext();){
			Literal lt = it.next();
			if(i !=0){
				s+= "or " +meaning.get(lt.entity)+"("+lt.position.x+","+lt.position.y+")";
			}
			else{
				s+=meaning.get(lt.entity)+"("+lt.position.x+","+lt.position.y+")";
			}
			i++;
		}
		return s;
	}
	
	private String describe(Literal entity){
		String s="";
		s = meaning.get(entity.entity)+"("+entity.position.x+","+entity.position.y+")";
		return s;
	}
	
	private boolean validIndex(int x, int y){
		if(x>9) return false;
		if(x<0) return false;
		if(y>9) return false;
		if(y<0) return false;
		return true;
	}
	
}
