import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
	static int DELAY = 150;
	int DELAY_new = DELAY;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int bodyParts_new = bodyParts;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
	int highScore;
	File file = new File("C:\\Users\\manup\\Desktop\\HighScore.txt");
	Timer timer;
	Random random;
	GamePanel game;
	JButton buttonPlay;
	
	
	GamePanel(){
				
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		this.addMouseListener(new MyMouseAdapter());

		startGame();
	}
	
	public void startGame() {

		newApple();
		running = true;
		timer = new Timer(DELAY_new,this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		
		if(running) {
			for(int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}
			
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			for(int i = 0; i < bodyParts_new; i++) {
				
				if(i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
					g.setColor(new Color(45,180,0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
			
		} else {
			
		    try {
		        BufferedReader reader = new BufferedReader(new FileReader(file));
		        String line = reader.readLine();
		        try { 
	                highScore = Integer.parseInt(line);
	                
	            } catch (NumberFormatException e1) {}
		        while (line != null)
		        {
		            try { 
		            	int currentScore = Integer.parseInt(line);
		                if (highScore < currentScore)                      
		                { 
		                	highScore = currentScore; 
		                }
		            } catch (NumberFormatException e1) {}
		            line = reader.readLine();
		        }
		        reader.close();

		    } catch (IOException ex) {
		        System.err.println("ERROR reading scores from file");
		    }
		    
		    if(highScore < applesEaten) {
		    	highScore = applesEaten;
		    }
		    
		    try {
		        BufferedWriter output = new BufferedWriter(new FileWriter(file, true));
		        output.append("" + applesEaten);
		        output.newLine();
		        output.close();

		    } catch (IOException ex1) {
		        System.out.printf("ERROR writing score to file: %s\n", ex1);
		    }
					
			gameOver(g);
		}
	}
	
	public void newApple() {
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
		
	}
	public void move() {
		for(int i = bodyParts_new; i > 0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}
	
	public void checkApple() {
		if((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts_new++;
			applesEaten++;
			if (DELAY_new > 70) {
				DELAY_new -= 5; 
				timer.stop();
				timer = new Timer(DELAY_new,this);
				timer.start();
			}			
			newApple();			
		}
	}
	
	public void checkCollisions() {
		// checks if head collides with body
		for(int i = bodyParts_new; i > 0; i--) {
			
			if((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		// checks if head touches left boarder
		if (x[0] < 0) {
			running = false;
		}
		// checks if head touches right boarder
		if (x[0] > SCREEN_WIDTH-2) {
			running = false;
		}
		// checks if head touches top boarder
		if (y[0] < 0) {
			running = false;
		}
		// checks if head touches bottom boarder
		if (y[0] > SCREEN_HEIGHT-2) {
			running = false;
		}
		
		if(!running) {
			timer.stop();
		}
	}
	
	
	public void gameOver(Graphics g) {
		//Scores
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
		//Game over text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over!", (SCREEN_WIDTH - metrics2.stringWidth("Game Over!"))/2, SCREEN_HEIGHT-250);
		g.setFont(new Font("Ink Free", Font.BOLD, 30));
		g.drawString("Highscore: "+highScore, SCREEN_WIDTH - 400, SCREEN_HEIGHT-430);

		g.drawString("Click to start\n new game!", SCREEN_WIDTH - 460, SCREEN_HEIGHT-30);
//		addbuttonPlay();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if(running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
		
	}
	
	public class MyMouseAdapter extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e) {
        	if (!running) {
        		DELAY_new = DELAY;
				direction = 'R';
				bodyParts_new = bodyParts;
				applesEaten = 0;
				for(int i = bodyParts_new-1; i >= 0; i--) {
					x[i] = UNIT_SIZE - ((i+1)*UNIT_SIZE);
					y[i] = 0;
				}
				startGame();
				repaint();
           }
        }
     }
	
	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
				}
				break;
			}
		}
	}
	
}
