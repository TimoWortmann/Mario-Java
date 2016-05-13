package Main;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Enemy.Test;
import Entity.Entity;
import Entity.Player;
import Input.Key;
import Input.Mouse;
import gfx.Sprite;
import gfx.Spritesheet;


public class Game extends Canvas implements Runnable{
	public static int breite = 270,h�he = breite/14*10,scale = 4;
	public boolean running = false;
	public Thread thread = new Thread();
	public static Handler handler = new Handler();
	public static Player player = new Player(1000,500,100,100,true,handler,Id.player);
	public static Camera cam;
	
	private BufferedImage image,background;
	public static Spritesheet sheet;
	public static Test test;
	
	public void init(){
		handler.addEntity(player);
		sheet = new Spritesheet("/Spritesheet.png");
		cam = new Camera();
	
		test = new Test(126,-10,30,30,true,handler,Id.enemy);

		addMouseListener(new Mouse());
		addKeyListener(new Key());
		try {
			image = ImageIO.read(getClass().getResource("/level.png"));
			background = ImageIO.read(getClass().getResource("/Background1.png"));
		} catch (IOException e) {}
		handler.createlevel(image);
		handler.addEnemy(test);
	}
	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs==null){
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.drawImage(background, 0, 0, getWidth(),getHeight(),null);
//		g.translate(cam.getX(), cam.getY()+220);
		handler.render(g);
		g.dispose();
		bs.show();
	}
	
	public void tick(){
		handler.tick();
		
		for(Entity e:handler.entity){
			if(e.getId()==Id.player){
				cam.tick(e);
			}
		}
		
	}
	
	public synchronized void start(){
		if(running) return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop(){
		if(!running) return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {}
	}
	
	 
	public void run(){
		init();
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		double delta = 0.0;
		double nanosecounds = 1000000000.0/60.0;
		int frames = 0;
		int ticks = 0;
		while(running){
			long now = System.nanoTime();
			delta+=(now-lastTime)/nanosecounds;
			lastTime = now;
			while(delta>=1){
				tick();
				ticks++;
				delta--;
			}
			render();
			frames++;
			if(System.currentTimeMillis()-timer>1000){
				timer+=1000;
				frames = 0;
				ticks = 0;
			}
		}
		stop();
	}
	
	public Game(){
		Dimension size = new Dimension(breite*scale,h�he*scale);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
	}
	
	public static int getFrameBreite(){
		return breite*scale;
	}
	
	public static int getFrameH�he(){
		return h�he*scale;
	}
	
	
}
