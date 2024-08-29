
public class Player {
	int health = 3;
	int score = 0;
	int posX = 0, posY = 0;
	boolean playerDown = false;
	
	public void subtractHealth() {
		this.health -=1;
	}
	public void setPlayerDown (boolean down) {
		playerDown = down;
	}
}
