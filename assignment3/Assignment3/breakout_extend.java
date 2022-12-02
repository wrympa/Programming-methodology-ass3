
/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class breakout_extend extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 51;

	private static final int PADDLE_EDGE_WIDTH = PADDLE_WIDTH / 3;

	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final double BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;
	/* Method: run() */
	/** Runs the Breakout program. */
	private GRect paddle1;
	private GRect paddle3;
	private GRect paddle2;
	private GRect brick;
	private GOval ball;
	private int tries = 6;
	private int points = 0;
	private double yloc = 0;
	private double xloc = 0;
	private double mouseX = 0;
	private double mouseY = 0;
	private double vx = 0;
	private double vy = 3;
	private double vxmin = 1.0;
	private double vxmax = 3.0;
	private boolean first = false;
	private boolean spedup = false;
	AudioClip bounceclip = MediaTools.loadAudioClip("vinethud.au");
	AudioClip cheer = MediaTools.loadAudioClip("cheer.au");
	AudioClip loss = MediaTools.loadAudioClip("loss.au");
	public RandomGenerator rgen = RandomGenerator.getInstance();
	private GImage img;
	private GOval xtra;
	private GLabel lives;
	private boolean spawned = false;
	
	// 4 variantidan 1 backgrounds irchevs
	public void DRAWBACKROUND() {
		int screen = rgen.nextInt(1, 4);
		if (screen == 1) {
			img = new GImage("bckg1.gif");
		}
		if (screen == 2) {
			img = new GImage("bckg2.gif");
		}
		if (screen == 3) {
			img = new GImage("bckg3.gif");
		}
		if (screen == 4) {
			img = new GImage("bckg4.gif");
		}
		double bx = img.getWidth();
		double by = img.getHeight();
		img.scale(WIDTH / bx, HEIGHT / by);
		add(img, 0, 0);
	}
	
	//extra lifebis damtvleli
	public void counter(int x)
	{
		lives = new GLabel("lives: " + (x-3));
		double lineW = (lives.getWidth());
		double lineH = (lives.getHeight());
		lives.setColor(Color.WHITE);
		add(lives, lineW/2, getHeight() - lineH/2);
	}

	// vxatavt blockebs
	public void DRAWBLOCKS() {
		for (int a = 0; a < NBRICK_ROWS; a++)
			for (int i = 0; i < NBRICKS_PER_ROW; i++) {
				brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (a < 2) {
					brick.setFillColor(Color.RED);
					brick.setColor(Color.RED);
				}
				if (a < 4 && a > 1) {
					brick.setFillColor(Color.ORANGE);
					brick.setColor(Color.ORANGE);
				}
				if (a < 6 && a > 3) {
					brick.setFillColor(Color.YELLOW);
					brick.setColor(Color.YELLOW);
				}
				if (a < 8 && a > 5) {
					brick.setFillColor(Color.GREEN);
					brick.setColor(Color.GREEN);
				}
				if (a < 10 && a > 7) {
					brick.setFillColor(Color.CYAN);
					brick.setColor(Color.CYAN);
				}
				add(brick, WIDTH - NBRICKS_PER_ROW * BRICK_WIDTH - BRICK_SEP * (NBRICKS_PER_ROW - 0.5)
						+ i * (BRICK_WIDTH + BRICK_SEP), a * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET);
			}
	}

	// extra lifes xatavs
	private void xtralife(double x, double y) {
		if (rgen.nextBoolean(0.1) == true && spawned == false) {
			xtra = new GOval(BALL_RADIUS, BALL_RADIUS);
			xtra.setFilled(true);
			xtra.setFillColor(Color.RED);
			add(xtra, x, y);
			spawned = true;
		}
	}

	// ekrani, romelic wagebisas gamochndeba
	public void lossscreen(double x, double y) {
		if ((yloc > HEIGHT - 2 * (BALL_RADIUS) - PADDLE_HEIGHT - PADDLE_Y_OFFSET) && tries == 1+NTURNS) {
			removeAll();
			if (spawned == true) {
				remove(xtra);
			}
			img = new GImage("lossscreen.gif");
			loss.play();
			double bx = img.getWidth();
			double by = img.getHeight();
			img.scale(WIDTH / bx, HEIGHT / by);
			add(img, 0, 0);

			GLabel top = new GLabel("YOU LOSE!!!!!!!!!!");
			double lineW = (top.getWidth());
			double lineH = (top.getHeight());
			top.setColor(Color.WHITE);
			add(top, getWidth() / 2 - lineW / 2, getHeight() / 2 - lineH / 2);

			waitForClick();
			exit();

		}
	}

	// ekrani, romelic mogebisas gamochndeba
	public void winscreen(double x, double y) {
		if (points ==  NBRICK_ROWS * NBRICKS_PER_ROW ) {
			removeAll();
			if (spawned == true) {
				remove(xtra);
			}
			img = new GImage("winscreen.gif");
			cheer.play();
			double bx = img.getWidth();
			double by = img.getHeight();
			img.scale(WIDTH / bx, HEIGHT / by);
			add(img, 0, 0);

			GLabel top = new GLabel("YOU WIN!!!!!!!!!!");
			double lineW = (top.getWidth());
			double lineH = (top.getHeight());
			top.setColor(Color.WHITE);
			add(top, getWidth() / 2 - lineW / 2, getHeight() / 2 - lineH / 2);

			waitForClick();
			exit();

		}
	}

	// xatavs 3 nawilian pedls
	private void DRAWPADDLE() {
		paddle1 = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle1.setFilled(true);
		paddle1.setFillColor(Color.WHITE);
		paddle1.setColor(Color.WHITE);
		add(paddle1, (getWidth() - PADDLE_WIDTH) / 2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		paddle2 = new GRect(PADDLE_EDGE_WIDTH, PADDLE_HEIGHT);
		paddle2.setFilled(true);
		paddle2.setFillColor(Color.WHITE);
		paddle2.setColor(Color.WHITE);
		add(paddle2, (getWidth() - PADDLE_WIDTH) / 2 + PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		paddle3 = new GRect(PADDLE_EDGE_WIDTH, PADDLE_HEIGHT);
		paddle3.setFilled(true);
		paddle3.setFillColor(Color.WHITE);
		paddle3.setColor(Color.WHITE);
		add(paddle3, (getWidth() - PADDLE_WIDTH) / 2 - PADDLE_EDGE_WIDTH,
				getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}

	// mousis wamkitxavi
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		mouseX = x;
		double y = e.getY();
		mouseY = y;
		MOVEPADDLE(x);
	}

	// amodzravebs padls
	public void MOVEPADDLE(double x) {

		if (x > WIDTH - PADDLE_WIDTH) {
			x = WIDTH - PADDLE_WIDTH;
		}
		paddle1.setLocation(x, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		paddle2.setLocation(x + PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		paddle3.setLocation(x - PADDLE_EDGE_WIDTH, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}

	// amowmebs top lefts
	private GObject TL() {
		GObject collider = null;
		collider = getElementAt(xloc + BALL_RADIUS / 4, yloc + BALL_RADIUS / 4);
		return collider;
	}

	// amowmebs top rights
	private GObject TR() {
		GObject collider = null;
		collider = getElementAt(xloc + 7 * BALL_RADIUS / 4, yloc + BALL_RADIUS / 4);
		return collider;
	}

	// amowmebs bottom rights
	private GObject BR() {
		GObject collider = null;
		collider = getElementAt(xloc + 7 * BALL_RADIUS / 4, yloc + 5 * BALL_RADIUS / 4);
		return collider;
	}

	// amowmebs bottom lefts
	private GObject BL() {
		GObject collider = null;
		collider = getElementAt(xloc + BALL_RADIUS / 4, yloc + 7 * BALL_RADIUS / 4);
		return collider;
	}

	// es otxi iyenebs zeda 4 funqcias
	public void checkTL() {
		if (TL() != ball && TL() != null && TL() != paddle1 && TL() != paddle2 && TL() != paddle3 && TL() != img && TL() != lives
				&& TL() != xtra) {
			if (vy < 0) {
				vy = vy * (-1);
			}
			if (vx < 0) {
				vx = vx * (-1);
			}
			remove(getElementAt(xloc + BALL_RADIUS / 5, yloc + BALL_RADIUS / 5));
			xtralife(xloc + BALL_RADIUS / 5, yloc + BALL_RADIUS / 5);
			points++;
			bounceclip.play();
		}
	}

	public void checkTR() {
		if (TR() != ball && TR() != null && TR() != paddle1 && TR() != paddle2 && TR() != paddle3 && TR() != img
				&& TR() != xtra && TR() != lives) {
			if (vy < 0) {
				vy = vy * (-1);
			}
			if (vx > 0) {
				vx = vx * (-1);
			}
			remove(getElementAt(xloc + 9 * BALL_RADIUS / 5, yloc + BALL_RADIUS / 5));
			xtralife(xloc + 9 * BALL_RADIUS / 5, yloc + BALL_RADIUS / 5);
			points++;
			bounceclip.play();
		}
	}

	public void checkBR() {
		if (BR() != ball && BR() != null && BR() != paddle1 && BR() != paddle2 && BR() != paddle3 && BR() != img
				&& BR() != xtra && BR() != lives) {
			if (vy > 0) {
				vy = vy * (-1);
			}
			if (vx > 0) {
				vx = vx * (-1);
			}
			remove(getElementAt(xloc + 9 * BALL_RADIUS / 5, yloc + 9 * BALL_RADIUS / 5));
			xtralife(xloc + 9 * BALL_RADIUS / 5, yloc + 9 * BALL_RADIUS / 5);
			points++;
			bounceclip.play();
		}
	}

	public void checkBL() {
		if (BL() != ball && BL() != null && BL() != paddle1 && BL() != paddle2 && BL() != paddle3 && BL() != img
				&& BL() != xtra && BL() != lives) {
			if (vy > 0) {
				vy = vy * (-1);
			}
			if (vx < 0) {
				vx = vx * (-1);
			}
			remove(getElementAt(xloc + BALL_RADIUS / 5, yloc + 9 * BALL_RADIUS / 5));
			xtralife(xloc + BALL_RADIUS / 5, yloc + 9 * BALL_RADIUS / 5);
			points++;
			bounceclip.play();
		}
	}

	// amowmebs shuawertlis
	public void checkmidpoint(double x, double y) {
		if (getElementAt(x, y) != paddle3 && getElementAt(x, y) != paddle2 && getElementAt(x, y) != paddle1
				&& getElementAt(x, y) != null && getElementAt(x, y) != ball && getElementAt(x, y) != xtra
				&& getElementAt(x, y) != img && getElementAt(x, y) != lives) {
			if (vy < 0) {
				vy = vy * (-1);
			}
			remove(getElementAt(x, y));
			xtralife(x, y);
			points++;
			if (points % 10 == 0 && spedup == false) {
				spedup = true;
			}
			bounceclip.play();
		}
	}

	// amowmebs yvela wertils
	public void checkcollision() {
		checkTL();
		checkmidpoint(xloc + BALL_RADIUS, yloc - 3);
		checkTR();
		checkmidpoint(xloc + 2 * BALL_RADIUS + 3, yloc + BALL_RADIUS);
		checkBR();
		checkmidpoint(xloc + BALL_RADIUS, yloc + 2 * BALL_RADIUS + 3);
		checkBL();
		checkmidpoint(xloc - 3, yloc + BALL_RADIUS);
	}

	// burtis gagdebis, lifeis dakargvis chekeri
	public void out() {
		if (yloc > HEIGHT - 2 * (BALL_RADIUS) - PADDLE_HEIGHT - PADDLE_Y_OFFSET && tries > NTURNS) {
			remove(ball);
			if (spawned == true) {
				remove(xtra);
			}
			tries--;
			remove(lives);
			counter(tries);
			DRAWBALL();
		}
	}

	// blokebis garda vertikalur collisions amowmebs
	public void ycoll() {
		if (paddle2.contains(xloc + BALL_RADIUS, yloc + 2 * BALL_RADIUS)
				|| paddle3.contains(xloc + BALL_RADIUS, yloc + 2 * BALL_RADIUS)) {
			if (vy > 0) {
				vy = vy * (-1);
			}
			bounceclip.play();
			vx = rgen.nextDouble(vxmin * 1.75, vxmax * 1.75);
			if (paddle3.contains(xloc + BALL_RADIUS, yloc + 2 * BALL_RADIUS)) {
				vx = vx * (-1);
			}
			first = false;
		} else if (paddle1.contains(xloc + BALL_RADIUS, yloc + 2 * BALL_RADIUS)) {
			if (vy > 0) {
				vy = vy * (-1);
			}
			bounceclip.play();
			if (first == false) {
				vx = rgen.nextDouble(vxmin, vxmax);
				if (rgen.nextBoolean(0.5)) {
					vx = vx * (-1);
				}
				first = true;
			}
		}
		if (yloc < 0) {
			if (vy < 0) {
				vy = vy * (-1);
			}
		}
	}

	// blokebis garda horizontalur collisions amowmebs
	public void xcoll() {
		if (xloc > WIDTH - 2 * BALL_RADIUS) {
			if (vx > 0) {
				vx = vx * (-1);
			}
			bounceclip.play();
		}
		if (xloc < 0) {
			if (vx < 0) {
				vx = vx * (-1);
			}
			bounceclip.play();
		}
	}

	// xatavs burts da amodzravebs
	public void DRAWBALL() {
		ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setFillColor(Color.WHITE);
		add(ball, WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS);
		first = false;
		vx = 0;
		waitForClick();
		while (true) {
			yloc = ball.getY();
			xloc = ball.getX();
			lossscreen(mouseX, mouseY);
			winscreen(mouseX, mouseY);
			out();
			ycoll();
			xcoll();
			pause(10);
			checkcollision();
			ball.move(vx, vy);
			if (spawned == true) {
				if (vy > 0) {
					xtra.move(0, 3 * vy / 5);
				} else {
					xtra.move(0, -3 * vy / 5);
				}
				double xx = xtra.getX();
				double xy = xtra.getY();
				if (getElementAt(xx, xy) == paddle1 || getElementAt(xx, xy) == paddle2
						|| getElementAt(xx, xy) == paddle3) {
					remove(xtra);
					tries++;
					spawned = false;
					remove(lives);
					counter(tries);
				} else if (xy > HEIGHT - BALL_RADIUS) {
					remove(xtra);
					spawned = false;
				}

			}
			if (points % 10 == 0 && points != 0 && spedup == true) {
				vx = vx * 1.5;
				spedup = false;
				vxmin = vxmin * 1.75;
				vxmax = vxmax * 1.75;
			}
		}
	}

	// add mouse listener da qmnis worlds
	public void init() {
		setSize(WIDTH, HEIGHT);
		addMouseListeners();
	}

	// yvelafers xatavs
	private void DRAWSPRITES() {
		DRAWBACKROUND();
		counter(tries);
		DRAWBLOCKS();
		DRAWPADDLE();
		DRAWBALL();
	}

	// ushvebs programas
	public void run() {
		DRAWSPRITES();
	}

}
