package wumpus;

import java.awt.Point;

public class Literal {

	public int entity;
	public Point position = new Point();
	
	public Literal(int entity, int x, int y) {
		this.entity = entity;
		position.x =x;
		position.y =y;
		
	}
	
}
