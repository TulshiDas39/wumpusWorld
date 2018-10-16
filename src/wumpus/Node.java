package wumpus;


import java.util.HashMap;
import java.util.HashSet;

public class Node {
	private HashSet<Integer>entities = new HashSet<Integer>();
	int imageIndex;
	
	
	public Node() {
		imageIndex = 0;
	}
	
	public void add(int num, HashMap<HashSet<Integer>, Integer> map){
		entities.add(num);
		imageIndex = map.get(entities);

	}

	public HashSet<Integer> getEntities(){
		return entities;
	}
	
	public void remove(int num, HashMap<HashSet<Integer>, Integer> map){
		entities.remove(Integer.valueOf(num));
		if(entities.isEmpty()) imageIndex =0;
		else imageIndex = map.get(entities);
	}
	
}
