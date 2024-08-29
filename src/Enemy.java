import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Enemy {
	boolean attack = false;
	int restX, restY, initX , type, initRX, initRY; //restX and Y are where enemy returns to after attack or initial swoop in
	int posX, posY;
	int idleCount = 0, deadCount = 0, exIndex = 0;
	double a = 0.0f;
	float speed = 8f;
	float idleSpeed;
	double distanceToCenterX, distanceToCenterY;
	int idleX, idleY, subX;
	int moveX, moveY, subY;
	float tempDistance = 0;
	int temp = 0, temp2 = 1, tempY, tempX = 0, initTemp = 1;
	boolean init, atHome = false, allAtHome = false, left = false, right = true, up = true, firstRun = true;
	boolean spriteChange = false;
	boolean isDead = false, explosion;
	char dir;
	ArrayList<Bullet> enemyBullets;
	Player player;
	BufferedImage returnSprite;
	TextureLoader tl;
	
	//swoop in coordinates
	int[] attack1X = new int[] {15, 35, 35, 15, -20, -40, -80, -100, -40, 0, 40, 15, -15, -25, -60, -60, -25, 0};
	int[] attack1Y = new int[] {-35, -15, 15, 35, 80, 80, 50, 70, 95, 95, 95, 60, 60, 60, 60, 70, 70, 380};
	
	int[] startX = new int[] {0, 100, 100, 100, 96, 44, -44, -106, -106, -44, 44, 106, 106, 44};
	int[] startY = new int[] {0, 50, 60, 80, 94, 106, 106, 44, -44, -106, -106, -44, 44, 106 };	
	
	public Enemy(int X, int Y, int restX, int restY, int type, Player player, TextureLoader tl, ArrayList<Bullet> enemyBullets) throws IOException {
		this.restX = restX;
		this.restY = restY;
		this.posX = X;
		this.posY = Y;
		this.initX = X;
		this.tempY = Y;
		this.type = type;
		this.player = player;
		this.tl = tl;
		this.enemyBullets = enemyBullets;
		returnSprite = tl.enemySprite.get(type*2);
		init = true;
		distanceToCenterX = (restX + 54) - 500;
		distanceToCenterY = restY;
		moveX = (int)(Math.round(distanceToCenterX/5));
		moveY = (int) (Math.round((distanceToCenterY/1000)*1000/3));
		if (initX <= 0) {
			tempX = 0;
		}
		else {
			tempX = 1000;
		}
	}
	public void swoopIn() {
		if (temp <= startX.length - 1) {
			if (hone(tempX, tempY, speed + 3)) {
				updateSprite();
				if (initX <= 0) {
					tempX += startX[temp];
					tempY = posY - startY[temp];
				}
				else if (initX >= 1000){
					tempX -= startX[temp];
					tempY = (posY - startY[temp]);
				}
				temp += 1;
			}
			a = a + Math.toRadians(90); //for sprite rotation
		}
		else {
			if (hone(restX, restY, speed + 3)) {
				updateSprite();
				init = false;
				atHome = true;
				temp = 0;
			}
			a = a + Math.toRadians(90); //for sprite rotation
		}

	}
	public void enemyIdle() {
		if (firstRun) {
			initRX = restX;
			initRY = restY;
			firstRun = false;
		}
		if (up)
			idleCount++;
		else
			idleCount--;
		if (idleCount >= 200) {
			up = false;
			idleCount = 0;
			idleX = 0;
			idleY = 0;
		}
			
		else if (idleCount <= -200) {
			up = true;
			idleCount = 0;
			idleX = 0;
			idleY = 0;
		}	
		if (idleCount%5 == 0) {
			if (up) {
				idleX += moveX;
				idleY += moveY;
			}
			else {
				idleX -= moveX;
				idleY -= moveY;
			}
			subX = (idleX) + restX*1000;		//for accurate double math
			subY = (idleY) + restY*1000;		//for accurate double math
			restX = (int) (Math.round((double)subX/1000));
			restY = (int) (Math.round((double)subY/1000)); //Math.round gives more accurate numbers than just converting to int for some reason
			//last position has 50 pixel error rounding, because you can never have too much. This keeps float arithmetic from doing funky things
			if (Math.sqrt(Math.pow((double)restX - initRX, 2) + Math.pow((double)restY - initRY, 2)) < 50 && !up && idleCount <= -195) {
				restX = initRX;
				restY = initRY;
				idleCount = -201;
			}
		}		
	}
	public void checkPlayerCollision() {
		//distance equation, enemy and player hitboxes are viewed as circles
		if (!player.playerDown && Math.sqrt(Math.pow((double)(player.posY) - (double)(posY), 2) + Math.pow((double)(player.posX) - (double)(posX), 2)) < 50) {
			player.setPlayerDown(true);
			explosion = true;
		}
	}
	public void attack() {
		if (temp < attack1X.length) {
			//hone will continue to be called until it returns true
			if (hone(tempX, tempY, speed - 2)) {
				updateSprite();	//sprite updates each time enemy reaches the next position
				if (temp == 5 || temp == 8) {	//enemy will drop bomb at 5th and 8th position in coordinates array
					enemyBullets.add(new Bullet(posX, posY, dir, player));
				}
				temp += 1;
				if (temp == attack1X.length)
					posY = -50; 
				else {
					if (restX > 500) {
						dir = 'L';
						tempX = posX + attack1X[temp];
					}
					else {
						dir = 'R';		//bullet direction
						tempX = posX - attack1X[temp];
					}
					tempY = posY + attack1Y[temp];
				}
			}
			a = a + Math.toRadians(90); //for sprite rotation, top will face direction
		}
		//if enemy has reached final coordinate in attack array, it will hone back home
		else {
			if (hone(restX, restY, speed - 3)) {
				updateSprite();
				atHome = true;
				attack = false;
				temp = 0;
			}
			a = a + Math.toRadians(90) * -1; //for sprite rotation, bottom will face direction
		}
	}
	public void update() {
		//if enemy has been hit or hit player, explosion will be true, so explosion function will run and nothing else
		if (explosion) {
			explosion();
			return;
		}
		if (init) {
			swoopIn();
		}
		else if (atHome) {
				
			if (allAtHome)
				enemyIdle();
			if (spriteChange) 
				returnSprite = tl.enemySprite.get(type*2);
			else
				returnSprite = tl.enemySprite.get(type*2+1);
				
				
			posX = restX;
			posY = restY;
			if (a != 0) {
				rotateToZero();
			}
		}
		else if (attack) {
			checkPlayerCollision();
			//enemyIdle will only update rest position, so it continues to update while enemy is attacking
			enemyIdle();
			attack();
		}
	}
	public void explosion() {
		deadCount++;
		if (deadCount >= 4) {
			a = 0;
			returnSprite = tl.enemyExplosion.get(exIndex);
			deadCount = 0;
			exIndex++;
			if (exIndex >= 5) 
				isDead = true;
		}
	}
	//movement
	public boolean hone(int x, int y, float honeSpeed) {
		a = Math.atan2((y-posY), (x-posX));
		posX += (int)(honeSpeed * Math.cos(a));
		posY += (int)(honeSpeed * Math.sin(a));
		return checkCoords(x, y);
	}
	public boolean checkCoords(int x, int y) {
		if (posX >= x- speed && posX <= x + speed && (posY >= y - speed && posY <= y + speed)) {
			posX = x;
			posY = y;
			return true;
			
		}
		return false;
	}
	//sprite
	public void rotateToZero() {
		//1 degree error rounding
		if (Math.toDegrees(a) <= 1 && Math.toDegrees(a) >= -1) {
			a = 0;
		}
		else if (Math.toDegrees(a) < 0) {
			a += -Math.toRadians(Math.toDegrees(a)/5);
		}
		else {
			a -= Math.toRadians(Math.toDegrees(a)/5);
		}
	}
	public void updateSprite() {
		if (this.returnSprite.equals(tl.enemySprite.get(type*2+1)))
			returnSprite = tl.enemySprite.get(type*2);
		else
			returnSprite = tl.enemySprite.get(type*2+1);
				
	}
	public void setRestX(int x) {
		restX = x;
	}
	public void setRestY (int y) {
		restY = y;
	}
}
