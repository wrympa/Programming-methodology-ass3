
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

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
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
	private GRect paddle;
	private GRect brick;
	private GOval ball;
	private int tries = 0;
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
	AudioClip bounceclip = MediaTools.loadAudioClip("bounce.au");
//xatavs blokebs
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

	public RandomGenerator rgen = RandomGenerator.getInstance();
//xatavs wagebis ekrans
	public void lossscreen() {
		if ((yloc > HEIGHT - 2 * (BALL_RADIUS) - PADDLE_HEIGHT - PADDLE_Y_OFFSET) && tries == NTURNS - 1) {
			removeAll();
			GLabel top = new GLabel("YOU LOSE!!!!!!!!!!");
			double lineW = (top.getWidth());
			double lineH = (top.getHeight());
			add(top, getWidth() / 2 - lineW / 2, getHeight() / 2 - lineH / 2);
			waitForClick();
			exit();
		}
	}
//xatavs mogebis ekrans
	public void winscreen() {
		if (points == NBRICK_ROWS * NBRICKS_PER_ROW) {
			removeAll();
			GLabel top = new GLabel("YOU WIN!!!!!!!!!!");
			double lineW = (top.getWidth());
			double lineH = (top.getHeight());
			add(top, getWidth() / 2 - lineW / 2, getHeight() / 2 - lineH / 2);
			waitForClick();
			exit();
		}
	}
//xatavs pedls
	private void DRAWPADDLE() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle, (getWidth() - PADDLE_WIDTH) / 2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}
//igebs mousis koordinats
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		mouseX = x;
		double y = e.getY();
		mouseY = y;
		MOVEPADDLE(x);
	}
//amodzravebs pedls
	public void MOVEPADDLE(double x) {

		if (x > 400 - PADDLE_WIDTH) {
			x = 400 - PADDLE_WIDTH;
		}
		paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}
//amowmebs pedltan shejaxebas
	private GObject getcollpadl() {
		GObject collider = null;
		collider = getElementAt(xloc, yloc + 2 * BALL_RADIUS);
		if (collider != paddle) {
			collider = getElementAt(xloc + BALL_RADIUS, yloc + 2 * BALL_RADIUS);
			if (collider != paddle) {
				collider = getElementAt(xloc + 2 * BALL_RADIUS, yloc + 2 * BALL_RADIUS);
			}
		}
		return collider;
	}
//shemdegi 8 amowmebs kutxees
	private GObject TL() {
		GObject collider = null;
		collider = getElementAt(xloc, yloc);
		return collider;
	}

	private GObject TR() {
		GObject collider = null;
		collider = getElementAt(xloc + 2 * BALL_RADIUS, yloc);
		return collider;
	}

	private GObject BR() {
		GObject collider = null;
		collider = getElementAt(xloc + 2 * BALL_RADIUS, yloc + 2 * BALL_RADIUS);
		return collider;
	}

	private GObject BL() {
		GObject collider = null;
		collider = getElementAt(xloc, yloc + 2 * BALL_RADIUS);
		return collider;
	}
//shlis elements koordinatze umatebs qulas da sound clips ushvebs
	private void bounce(double x, double y) {
		remove(getElementAt(x, y));
		points++;
		bounceclip.play();
	}

	public void checkTL() {
		if (TL() != paddle && TL() != null) {
			if (vx > 0 && vy < 0) {
				vy = vy * (-1);
				remove(getElementAt(xloc, yloc));
				points++;
				bounceclip.play();
			} else if (TR() != paddle && TR() != null) {
				if (vy < 0) {
					vy = vy * (-1);
					bounce(xloc, yloc);
				}
				if (TR() != paddle && TR() != null) {
					bounce(xloc + 2 * BALL_RADIUS, yloc);
				}
			} else {
				if (vy < 0 && vx < 0) {
					vy = vy * (-1);
					vx = vx * (-1);
					bounce(xloc, yloc);
				}
			}
		}
	}
	

	public void checkTR() {
		if (TR() != paddle && TR() != null) {
			if (vx < 0 && vy < 0) {
				vy = vy * (-1);
				bounce(xloc + 2 * BALL_RADIUS, yloc);
			} else {
				if (vy < 0 && vx > 0) {
					vy = vy * (-1);
					vx = vx * (-1);
					bounce(xloc + 2 * BALL_RADIUS, yloc);
				}
			}
		}
	}

	public void checkBR() {
		if (BR() != paddle && BR() != null) {
			if (vx < 0 && vy > 0) {
				vy = vy * (-1);
				bounce(xloc + 2 * BALL_RADIUS, yloc + 2 * BALL_RADIUS);
			} else if (BL() != paddle && BL() != null) {
				if (vy > 0) {
					vy = vy * (-1);
					bounce(xloc + 2 * BALL_RADIUS, yloc + 2 * BALL_RADIUS);
				}
				if (BL() != paddle && BL() != null) {
					bounce(xloc, yloc + 2 * BALL_RADIUS);
				}
			} else {
				if (vy > 0 && vx < 0) {
					vy = vy * (-1);
					bounce(xloc + 2 * BALL_RADIUS, yloc + 2 * BALL_RADIUS);
					vx = vx * (-1);
				}
			}
		}
	}

	public void checkBL() {
		if (BL() != paddle && BL() != null) {
			if (vx > 0 && vy > 0) {
				vy = vy * (-1);
				bounce(xloc, yloc + 2 * BALL_RADIUS);
			}
			if (BR() != paddle && BR() != null) {
				if (vy > 0) {
					vy = vy * (-1);
					bounce(xloc, yloc + 2 * BALL_RADIUS);
				}

				if (BR() != paddle && BR() != null) {
					bounce(xloc + 2 * BALL_RADIUS, yloc + 2 * BALL_RADIUS);
				}

			} else {
				if (vy > 0 && vx < 0) {
					vy = vy * (-1);
					bounce(xloc, yloc + 2 * BALL_RADIUS);
					vx = vx * (-1);
				}
			}
		}
	}
	//yvela checks tavs uyris
	public void checkcollision() {
		checkTL();
		checkTR();
		checkBR();
		checkBL();
	}
//burtis out of bounds gasvlas amowmebs
	public void out() {
		if (yloc > HEIGHT - 2 * (BALL_RADIUS) - PADDLE_HEIGHT - PADDLE_Y_OFFSET && tries < NTURNS) {
			tries++;
			remove(ball);
			DRAWBALL();
		}
	}
//vertikalur collision
	public void ycoll() {
		if (getcollpadl() == paddle) {
			if (vy > 0) {
				vy = vy * (-1);
			}
			bounceclip.play();
			if (first == false) {
				vx = rgen.nextDouble(vxmin, vxmax);
				;
				if (rgen.nextBoolean(0.5)) {
					vx = vx * (-1);
				}
				first = true;
			}
		}
		if (yloc < 0) {
			if (vy < 0) {
				vy = vy * (-1);
				bounceclip.play();
			}
		}
	}
//horizontalur collision
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
//xatavs burts da amodzravebs
	public void DRAWBALL() {

		ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball, WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS);
		first = false;
		vx = 0;
		waitForClick();
		while (true) {
			yloc = ball.getY();
			xloc = ball.getX();
			lossscreen();
			out();
			ycoll();
			xcoll();
			pause(10);
			checkcollision();
			winscreen();
			ball.move(vx, vy);
			paddle.setLocation(xloc - BALL_RADIUS, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}
//set size da amatebs mouse listeners
	public void init() {
		setSize(WIDTH, HEIGHT);
		addMouseListeners();
	}
//yvelaferi gavaertiane
	private void DRAWSPRITES() {
		DRAWBLOCKS();
		DRAWPADDLE();
		DRAWBALL();
	}
//mteli programa
	public void run() {
		DRAWSPRITES();
	}

}
