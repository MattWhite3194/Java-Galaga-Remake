
public class Bullet {
	char direction;
	int posX, posY;
	boolean enemyBullet = false;
	boolean hitPlayer = false;
	Player player;
	
	public Bullet(int X, int Y, char dir) {
		this.posX = X;
		this.posY = Y;
		this.direction = dir;
	}
	public Bullet(int X, int Y, char dir, Player player) {
		this.posX = X;
		this.posY = Y;
		this.direction = dir;
		this.player = player;
		enemyBullet = true;
	}
	public void update() {
		switch (direction) {
		case 'U' :
			posY -= 20;
			break;
		case 'R' :
			posY += 12;
			posX += 2;
			break;
		case 'L' :
			posY += 12;
			posX -= 2;
		}
		if (enemyBullet) 
			checkCollision();
	}
	public void checkCollision() {
		if (Math.sqrt(Math.pow(player.posX - posX, 2) + Math.pow(player.posY - posY, 2)) < 40) {
			player.setPlayerDown(true);
			hitPlayer = true;
		}
	}
}
