import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class TextureLoader {
	BufferedImage enemy1Sprite;
	BufferedImage enemy1Sprite2;
	BufferedImage BulletSprite;
	BufferedImage EBulletSprite;
	BufferedImage Boom;
	ArrayList<BufferedImage> enemySprite = new ArrayList<>();
	ArrayList<BufferedImage> enemyExplosion = new ArrayList<>();
	ArrayList<BufferedImage> playerExplosion = new ArrayList<>();
	BufferedImage playerSprite;
	public TextureLoader() throws IOException {
		playerSprite = ImageIO.read(TextureLoader.class.getResourceAsStream("/Textures/PlayerShip.png"));
		BulletSprite = ImageIO.read(TextureLoader.class.getResourceAsStream("/Textures/Bullet/PlayerBullet.png"));
		EBulletSprite = ImageIO.read(TextureLoader.class.getResourceAsStream("/Textures/Bullet/EnemyBullet.png"));
		enemy1Sprite = ImageIO.read(TextureLoader.class.getResourceAsStream("/Textures/enemy1-1.png"));
		enemy1Sprite2 = ImageIO.read(TextureLoader.class.getResourceAsStream("/Textures/enemy1-2.png"));
		Boom = ImageIO.read(TextureLoader.class.getResourceAsStream("/Textures/Boom.png"));
		for (int i = 0; i < 4; i++) {	//2*#of_enemies
			enemySprite.add(ImageIO.read(TextureLoader.class.getResourceAsStream("/Textures/enemy" + (i/2+1) + "-" + (i%2+1) + ".png")));
		}
		for (int i = 0; i < 5; i++) {
			enemyExplosion.add(ImageIO.read(TextureLoader.class.getResourceAsStream("/Textures/EnemyExplosion/enemyEx" + (i + 1) + ".png")));
		}
		for (int i = 0; i < 4; i++) {
			playerExplosion.add(ImageIO.read(TextureLoader.class.getResourceAsStream("/Textures/PlayerExplosion/playerX" + (i + 1) + ".png")));
		}
	}
}
