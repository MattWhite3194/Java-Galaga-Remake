import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Panel extends JPanel implements Runnable {
	
	
	//Hello World
	private static final long serialVersionUID = 1L;
	int starsX[] = new int [50];
	int starsY[] = new int[50];
	Color starColors[] = new Color[50];
	ArrayList<Enemy> enemies = new ArrayList<>();
	ArrayList<Bullet> bullets = new ArrayList<>();
	ArrayList<Bullet> enemyBullets = new ArrayList<>();
	TextureLoader tl = new TextureLoader();
	Player player = new Player();
	ShootTimer shootTimer;
	EnemyTimer et;
	LevelTimer lt;
	Random random = new Random();
	AffineTransform enemy;
	int level = 0;
	int rand, attackRand;
	double enemyAngleToCenter;
	int attackCt, attackDelay = 10, playerWait;
	boolean enemiesLoaded = false;
	boolean canUpdate = true;
	boolean timerRunning = false;
	int enemyInitIdleDisplacement = 0;
	boolean firstRow = true;
	int enemyRows = 0;
	BufferedImage playerSprite;
	int score = 0;
	int highScores[];
	String highScore = "000000";
	String highScoreNames[];
	int numScoresStored;
	//enemy grid
	int topRowWidth = (52)*7 + 140, RowWidth, unitSize = 70, displace;
	public static final int UPDATE_SPEED= 50;
	boolean moving = false, gameRunning = true, canShoot = true, shooting = false;
	char direction = 'd';
	Font retroFont;
	
	
	//comments are so I don't forget anything, I'm typing this as I've forgotten everything
	public Panel() throws IOException {
		
		this.setBackground(Color.black);
		this.setPreferredSize(new Dimension(1000, 1350));
		this.setFocusable(true);
		this.requestFocus();
		this.addKeyListener(new GameKeyListener());
		
		//array of random star coordinates
		int n;
		for (int i = 0; i < 50; i++) { 
			n = random.nextInt(1000);
			starsX[i] = n;					
			starsY[i] = random.nextInt(1350);
			starColors[i] = new Color(random.nextInt(150, 255), random.nextInt(150, 255), random.nextInt(150, 255)); 
		}
		
		//player starting position
		player.posX = 500;
		player.posY = 1200;
		et = new EnemyTimer(50);
		lt = new LevelTimer(2000);
		shootTimer = new ShootTimer(400);
		playerSprite = tl.playerSprite;
		
		getHighScores("/Scores/HighScores.txt");
		loadFont("res/Font/RetroFont.ttf");
		//g2d.setFont(retroFont);
		Thread th = new Thread(this);
		th.start();
	}
	
	@Override
	public void run(){
		while (gameRunning) {	//while loop, run is only called once
			update();
			repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw((Graphics2D) g);
	}
	public void draw(Graphics2D g) {
		g.setFont(retroFont);
		//draw player health+
		for (int i = 0; i < player.health; i++) {
			g.drawImage(tl.playerSprite, i*64 + i*10, 1350-70, 60, 64, null);
		}

		//draw background stars
		for (int i = 0; i < starsX.length; i++) {
			if (i != rand && i != rand + 10) {
				g.setColor(starColors[i]);
				g.fillRect(starsX[i], starsY[i], 5, 5);
			}
			starsY[i] += 5;
			if (starsY[i] > 1350)
				starsY[i] = 0;
		}
		//draw player
		g.drawImage(playerSprite, player.posX - playerSprite.getWidth() + 5, player.posY - playerSprite.getHeight() + 5, playerSprite.getWidth()*2, playerSprite.getHeight()*2, null);

		//draw bullets
		for (int i = 0; i < bullets.size(); i++) {
			g.drawImage(tl.BulletSprite, bullets.get(i).posX, bullets.get(i).posY, 12, 32, null);
		}
		for (int i = 0; i < enemyBullets.size(); i++) {
			g.drawImage(tl.EBulletSprite, enemyBullets.get(i).posX, enemyBullets.get(i).posY, 12, 32, null);
		}
		
		//draw enemies
		for (int i = 0; i < enemies.size(); i++) {
			enemy = AffineTransform.getRotateInstance(enemies.get(i).a, enemies.get(i).posX, enemies.get(i).posY);
			g.setTransform(enemy);	//sets enemies rotation
			g.drawImage(enemies.get(i).returnSprite, enemies.get(i).posX - enemies.get(i).returnSprite.getWidth(), enemies.get(i).posY - enemies.get(i).returnSprite.getHeight(), enemies.get(i).returnSprite.getWidth()*2, enemies.get(i).returnSprite.getHeight()*2, null);
		}

		//draw text
		g.setColor(Color.red);
		g.drawString("1UP", 20, 35);
		g.drawString("High Score", 500, 35);
		g.setColor(Color.white);
		g.drawString(String.format("%06d", score), 20, 75);
		g.drawString(highScore, 500, 75);


		g.dispose();
	}
	
	
	
	public void update() {
		if (player.playerDown) {
			moving = false;
			if (player.health <= 0) {
				recordScores("/Scores/HighScores.txt");
				gameRunning = false;
			}
		}
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).update();
			if (enemies.get(i).isDead) {
				enemies.remove(i);
				i--;
			}
		}
		
		
		if (moving) {
			if (direction == 'a')
				player.posX -=4;
			if (direction == 'd')
				player.posX +=4;
		}
		if (player.posX < 50 && !player.playerDown)
			player.posX = 50;
		else if (player.posX > 950 && !player.playerDown) 
			player.posX = 950;
		
		if (shooting && canShoot) {
			shoot(player.posX, player.posY);
			canShoot = false;
			shootTimer.start();
		}
		
		
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update();
			if(bullets.get(i).posY < 0) {
				bullets.remove(i);
				i--;
			}
		}
		
		for (int i = 0; i < enemyBullets.size(); i++) {
			enemyBullets.get(i).update();
			if(enemyBullets.get(i).posY > 1350 || enemyBullets.get(i).hitPlayer) {
				enemyBullets.remove(i);
				i--;
			}
		}
		try {
			checkLevel();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (!enemies.isEmpty() && !bullets.isEmpty()) {
			checkBulletCollision();
		}
	}

	public void shoot(int posX, int posY) {
		bullets.add(new Bullet(posX, posY, 'U'));
	}
	
	public void checkLevel() throws IOException {
		if (enemies.isEmpty() && canUpdate) {
			enemiesLoaded = false;
			level += 1;
			enemyRows = level*2;
			if (enemyRows > 8)
				enemyRows = 8;	//maximum of 8 enemy rows
			canUpdate = false;
			firstRow = true;
		}
		if(enemyRows == 0) {
			enemiesLoaded = true;
			canUpdate = true;
		}
		if (!enemiesLoaded && !timerRunning) {
			lt.start();
			timerRunning = true;
		}
	}
	public void checkBulletCollision() {
		for (int i = 0; i < bullets.size(); i++) {
			for (int j = 0; j < enemies.size(); j++) {
				if (!enemies.get(j).explosion && Math.sqrt(Math.pow((double)(bullets.get(i).posY + 16) - (double)(enemies.get(j).posY), 2) + Math.pow((double)(bullets.get(i).posX + 6) - (double)(enemies.get(j).posX), 2)) < 2 + 26) {
					enemies.get(j).hit();
					bullets.remove(i);
					score += 50;
					break;
				}
			}
		}
	}
	public void attack() {
		attackRand = random.nextInt(enemies.size());
		if (random.nextInt(4) == 1 && !enemies.get(attackRand).attack)
		{
			enemies.get(attackRand).attack = true;
			enemies.get(attackRand).atHome = false;
			enemies.get(attackRand).tempX = enemies.get(attackRand).restX + enemies.get(attackRand).attack1X[0];
			enemies.get(attackRand).tempY = enemies.get(attackRand).restY + enemies.get(attackRand).attack1Y[0];
		}
	}

	private void loadFont(String filePath) {
		try {
			InputStream fontFile = new BufferedInputStream(new FileInputStream(filePath));
			retroFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			GraphicsEnvironment gEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			gEnvironment.registerFont(retroFont);
			retroFont = retroFont.deriveFont(40.0f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	private void getHighScores(String filePath) throws FileNotFoundException {
		File scoresFile = new File(filePath);
		if (!scoresFile.exists())
			return;
		Scanner scanner = new Scanner(scoresFile);
		if (scanner.hasNextInt())
			numScoresStored = scanner.nextInt();
		else {
			scanner.close();
			return;
		}
		highScores = new int[numScoresStored];
		highScoreNames = new String[numScoresStored];
		for (int i = 0; i < numScoresStored; i++) {
			if (scanner.hasNext())
				highScoreNames[i] = scanner.next();
			if (scanner.hasNextInt())
				highScores[i] = scanner.nextInt();
		}
		highScore = String.format("%06d", highScores[0]);
		scanner.close();
	}

	private void recordScores(String filePath) {
		
	}
	
	public class GameKeyListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			//Method not needed
		}

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyChar()) {
			case 'a' :
				if (player.posX > 50) {
					direction = 'a';
					moving = true;
				}
				else
					moving = false;
				break;
			case 'd' :
				if (player.posX < 950) {
					direction = 'd';
					moving = true;
				}
				else
					moving = false;
				break;
			case ' ' :
				if (canShoot && !player.playerDown) {
					shooting = true;
				}
				break;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyChar()) {
			case 'a' : 
				if (direction == 'a')
					moving = false;
				break;
			case 'd' :
				if (direction == 'd')
					moving = false;
				break;
			case ' ' : 
				shooting = false;
				break;
			}
		}		
	}
	public class ShootTimer implements ActionListener {
		Timer timer;
		public ShootTimer (int delay) {
			timer = new Timer(delay, this);
			timer.start();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			timer.stop();
			canShoot = true;
		}
		public void start() {
			timer.start();
		}
	}
	public class LevelTimer implements ActionListener {
		Timer timer;
		public LevelTimer (int delay) {
			timer = new Timer(delay, this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			timer.stop();
			for (int i = 0; i < 8; i++) {
				try {
					if (firstRow) 
						et.reset();
					displace = ((1000 - 7*unitSize)/2) + (i * unitSize) + enemyInitIdleDisplacement;
					if (enemyRows%2 == 0) {
						enemies.add(new Enemy(-(8-i) * unitSize, 15 * unitSize, displace, unitSize * enemyRows, 1, player, tl, enemyBullets));
					}
					else
					{
						enemies.add(new Enemy(1000 + (i * unitSize), 15 * unitSize, displace, unitSize * enemyRows, 0, player, tl, enemyBullets));
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			timerRunning = false;
			firstRow = false;
			enemyRows -= 1;
		}
		public void start() {
			timer.start();
		}
	}
	public class EnemyTimer implements ActionListener {
		Timer timer;
		int temp = 1, starTemp = 0, spriteTemp = 0, PXIndex = 0;
		boolean init = true;
		boolean left = false, right = true;
		public EnemyTimer (int delay) {
			timer = new Timer(delay, this);
			timer.start();
		}
		public void checkHome() {
			if (enemiesLoaded) {
				for (int i = 0; i < enemies.size(); i++) {
					if (!enemies.get(i).atHome)
						return;
				}
				if (temp == 0) {
					for (int i = 0; i < enemies.size(); i++) {
						enemies.get(i).allAtHome = true;
					}
					init = false;
				}
			}
		}
		public void initEnemyIdle() {
			if (!enemies.isEmpty()) {
				if (temp >= 50) {
				left = true;
				right = false;
				}
				else if (temp <= -50) {
					left = false;
					right = true;
				}
				
				if (left) {
					temp -=1;
					for (int i = 0; i < enemies.size(); i++) {
						enemies.get(i).setRestX(enemies.get(i).restX - 1);
					}
				}
				else if (right) {
					temp += 1;
					for (int i = 0; i < enemies.size(); i++) {
						enemies.get(i).setRestX(enemies.get(i).restX + 1);
					}
				}
				enemyInitIdleDisplacement = temp;
			}
		}
		public void deadPlayer() {
			playerWait++;
			if (PXIndex <= 3 && playerWait % 2 == 0) {
				playerSprite = tl.playerExplosion.get(PXIndex);
				PXIndex++;
			}
			else if (PXIndex == 4){
				player.posX = -100;
				playerSprite = tl.playerSprite;
			}
			if (playerWait >= 75) {
				player.setPlayerDown(false);
				playerWait = 0;
				player.posX = 500;
				player.posY = 1200;
				player.subtractHealth();
				PXIndex = 0;
			}
		}
		public void updateStars() {
			starTemp++;
			if (starTemp >= 3) {
				rand = random.nextInt(50);
				starTemp = 0;
			}
		}
		public void spriteUpdate() {
			spriteTemp++;
			if (spriteTemp >= 14) {
				if (!enemies.isEmpty()) {
					for (int i = 0; i < enemies.size(); i++) 
						enemies.get(i).spriteChange = !enemies.get(i).spriteChange;
				}
				spriteTemp = 0;
			}
		}
		public void reset() {
			temp = 1;
			init = true;
			right = true;
			left = false;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			updateStars();
			spriteUpdate();
			if (player.playerDown) {
				deadPlayer();
			}
			
			//if all enemies have reached home after initial swoop in
			if (init) {
				initEnemyIdle();
				checkHome();
				return;
			}
			
			//timer for enemy attack, will start once all enemies are at home after initial swoop in
			if (!player.playerDown) {
				attackCt++;
				if (attackCt >= attackDelay && !enemies.isEmpty()) {
					attack();
					attackCt = 0;
				}
			}	
		}
	}
}
