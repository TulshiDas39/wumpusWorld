package wumpus;

import java.util.HashSet;

public class Statement {

	 public HashSet<Literal> literals = new HashSet<Literal>();
	 public double probablity = 0.5;
	 
	 public Statement(HashSet<Literal> literals2) {
		this.literals = literals2;
	}
	 
	 
}
