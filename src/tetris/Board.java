package tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import tetris.Shape.Tetrominoes;


class Point2D
{  float x, y;
Point2D(float x, float y){this.x = x; this.y = y;}
}

public class Board extends JPanel implements ActionListener {

	int BoardWidth = 10;
	int BoardHeight = 20;
	final int NextBoardWidth = 5;
	final int NextBoardHeight = 7;
	int scoringFactor=1;
	int rowsForLevel=2;
	float speedFactor=0.1f;
	int score=0;
	int level=1;
	int lines=0;
	float fallingSpeed=.005F;
	float timerDuration=1/fallingSpeed;
	JComboBox comboBoxScoringFactor ;
	JComboBox comboBoxNL ;
	JComboBox comboBoxSF;
	JComboBox comboBoxMW;
	JComboBox comboBoxMH;

	float mainAreaHeight;
	float mainAreaWidth;
	int maxX, maxY, minMaxXY, xCenter, yCenter;
	public boolean pauseButton;
	boolean flagForPause;
	ArrayList<Integer> excludeRandom= new ArrayList<Integer> ();
	ArrayList<Integer> excludeNewPieces= new ArrayList<Integer> ();

	Rectangle mainArea, quitArea;
	Point2D[] mainAreaRectangle=new Point2D[4];
	int curX = 0;
	int curY = 0;
	Shape curPiece;
	Shape nextPiece;
	JLabel statusbar;
	int numLinesRemoved = 0;
	Tetris parent;

	Tetrominoes[] board;
	Tetrominoes[] nextBoard;

	Timer timer;
	boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;

	public Board(Tetris parent,ArrayList<Integer> excludeNewPieces,  int mainHeight, int mainWidth) {
		this.parent=parent;
		setFocusable(true);
		BoardWidth=mainWidth;
		this.excludeNewPieces= excludeNewPieces;


		board = new Tetrominoes[BoardWidth * BoardHeight];
		nextBoard = new Tetrominoes[NextBoardWidth * NextBoardHeight];
		pauseButton = false;
		flagForPause= true;
		curPiece = new Shape();
		nextPiece = new Shape();
		clearBoard();
		clearNextBoard();
		timer = new Timer((int)timerDuration, this);
		timer.start();
		addKeyListener(new TAdapter());

		statusbar = parent.getStatusBar();

		addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				if (!pauseButton) {
					if (e.getWheelRotation() < 0) {
						tryMove(curPiece.rotateLeft(), curX, curY);
					} else {
						tryMove(curPiece.rotateRight(), curX, curY);
					}
					if (e.isControlDown()) {

					} else {
						getParent().dispatchEvent(e);
					}

				}
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {


				Point2D p= new Point2D(e.getX(),e.getY());

				boolean ifInside=pointInsidePolygon(p,mainAreaRectangle);

				if (ifInside && flagForPause) {
					pauseButton = true;
					repaint();
					flagForPause = false;
					pause();
				}
				if (!ifInside && !flagForPause) {
					flagForPause = true;
					pauseButton = false;
					repaint();
					pause();
				}

				;

			}

		});



		createSettingFrame(this);





		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (!pauseButton) {
					if (SwingUtilities.isLeftMouseButton(evt)) {
						tryMove(curPiece, curX - 1, curY);
					} else if (SwingUtilities.isRightMouseButton(evt)) {
						tryMove(curPiece, curX + 1, curY);

					}
				}


				if (quitArea.contains(evt.getPoint())) {
					System.exit(0);

				}



			}
		});
	}



	private void createSettingFrame(Board board2) {
		board2.setLayout(new BorderLayout());
		JPanel buttonPanel2 = new JPanel();
		buttonPanel2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton clickmeButton = new JButton("Settings ");
		clickmeButton.setFocusable(false);
		buttonPanel2.add(clickmeButton);


		JButton reset = new JButton("Reset ");
		buttonPanel2.add(reset);
		reset.setFocusable(false);


		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				start();



			}
		});



		board2.add(buttonPanel2,BorderLayout.SOUTH);





		JButton Change = new JButton("Change ");






		clickmeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pause();
				board2.setVisible(false);
				JFrame setting = new JFrame();
				setting.setSize(1200, 600);
				setting.setLayout(new FlowLayout(FlowLayout.LEFT));
				setting.add(new JLabel("Scoring Factor"));
				//  setting.add(btnUpdate);
				setting.setVisible(true);
				int[] values = {1,2,3,4,5,6,7,8,9,10};
				DefaultComboBoxModel model = new DefaultComboBoxModel();
				for (int value : values) {
					model.addElement(value);
				}
				comboBoxScoringFactor = new JComboBox(model);

				setting.add(comboBoxScoringFactor);


				setting.add(new JLabel("   Number of Rows for each Level"));

				setting.setVisible(true);
				int[] valuesNL = {20,30,40,50};
				DefaultComboBoxModel modelNL = new DefaultComboBoxModel();
				for (int value : valuesNL) {
					modelNL.addElement(value);
				}
				comboBoxNL = new JComboBox(modelNL);

				setting.add(comboBoxNL);

				setting.add(new JLabel("   Speed Factor"));
				//  setting.add(btnUpdate);
				setting.setVisible(true);
				float[] valuesSF = {0.1F,0.2F,0.3F,0.4F,0.5F,0.6F,0.7F,0.8F,0.9F,1.0F};
				DefaultComboBoxModel modelSF = new DefaultComboBoxModel();
				for (float value : valuesSF) {
					modelSF.addElement(value);
				}
				comboBoxSF= new JComboBox(modelSF);

				setting.add(comboBoxSF);




				setting.add(Change);


				Change.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						changeParameters();
						setting.setVisible(false);
						setting.dispose();
						pause();
						board2.setVisible(true);
						board2.start();

					}


				});




				setting.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						pause();
						board2.setVisible(true);


					}
				});

			}
		});



	}
	private void changeParameters() {
		scoringFactor= (int)comboBoxScoringFactor.getSelectedItem();
		rowsForLevel= (int)comboBoxNL.getSelectedItem();
		speedFactor= (float) comboBoxSF.getSelectedItem();


	}

	float area2(Point2D A, Point2D B, Point2D C)
	{  return (A.x - C.x) * (B.y - C.y) - (A.y - C.y) * (B.x - C.x);
	}


	boolean pointInsidePolygon(Point2D p, Point2D[] pol) {
		int n = pol.length, j = n - 1;
		boolean b = false;
		float x = p.x, y = p.y;
		for (int i = 0; i < n; i++) {
			if (pol[j].y <= y && y < pol[i].y && area2(pol[j], pol[i], p) > 0
					|| pol[i].y <= y && y < pol[j].y && area2(pol[i], pol[j], p) > 0)
				b = !b;
			j = i;
		}
		return b;
	}

	void initgr() {
		Dimension d = getSize();
		maxX = d.width - 1;
		maxY = d.height - 1;
		minMaxXY = Math.min(maxX, maxY);
		xCenter = maxX / 2;
		yCenter = maxY / 2;
		mainAreaHeight = 0.95F * minMaxXY;
		mainAreaWidth = mainAreaHeight * 0.5F;
	}
	//region abc
	public void start() {
		if (isPaused)
			return;

		isStarted = true;
		isFallingFinished = false;
		numLinesRemoved = 0;
		lines=0;
		score=0;
		level=1;

		clearBoard();
		excludeRandom.clear();
		setNextPiece();
		newPiece();
		timer.start();
		statusbar.setText(String.valueOf(numLinesRemoved));
	}
	//endregion abc
	public void actionPerformed(ActionEvent e) {
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			oneLineDown();
		}
	}

	private void pause() {
		if (!isStarted)
			return;

		isPaused = !isPaused;
		if (isPaused) {
			timer.stop();
			statusbar.setText("paused");
		} else {
			timer.start();
			resetPieces();
			statusbar.setText(String.valueOf(numLinesRemoved));

		}
		repaint();
	}

	private void resetPieces() {

		do {
			excludeRandom.add(curPiece.getShape().ordinal());
			curPiece.setRandomShape(excludeRandom,excludeNewPieces);
		}while(!tryMove(curPiece, curX, curY));
		excludeRandom.clear();
		excludeRandom.add(nextPiece.getShape().ordinal());
		if(score>=level*scoringFactor) {
			score=score-level*scoringFactor;
		}




	}

	private void newPiece()

	{
		clearNextBoard();

		curPiece.setShape(nextPiece.getShape());
		setNextPiece();


		curX = BoardWidth / 2 - 1;
		curY = BoardHeight - 1 + curPiece.minY();

		for (int i = 0; i < 4; ++i) {
			int x = 3 + nextPiece.x(i);
			int y = 4 - nextPiece.y(i);
			nextBoard[(y * NextBoardWidth) + x] = nextPiece.getShape();
		}

		if (!tryMove(curPiece, curX, curY)) {
			curPiece.setShape(Tetrominoes.NoShape);
			timer.stop();
			isStarted = false;
			statusbar.setText("game over");
		}

	}

	private void setNextPiece() {
		nextPiece.setRandomShape(excludeRandom,excludeNewPieces);
		excludeRandom.clear();
		excludeRandom.add(nextPiece.getShape().ordinal());	
		clearNextBoard();
		for (int i = 0; i < 4; ++i) {
			int x = 3 + nextPiece.x(i);
			int y = 4 - nextPiece.y(i);
			nextBoard[(y * NextBoardWidth) + x] = nextPiece.getShape();
		}
	}

	private void clearBoard() {
		for (int i = 0; i < BoardHeight * BoardWidth; ++i)
			board[i] = Tetrominoes.NoShape;

	}


	private void clearNextBoard() {

		for (int i = 0; i < NextBoardHeight * NextBoardWidth; ++i)
			nextBoard[i] = Tetrominoes.NoShape;

	}

	private boolean tryMove(Shape newPiece, int newX, int newY) {
		for (int i = 0; i < 4; ++i) {
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			if (shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}

	private void oneLineDown() {
		if (!tryMove(curPiece, curX, curY - 1)) {

			pieceDropped();
		}
	}

	private void pieceDropped() {
		for (int i = 0; i < 4; ++i) {
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BoardWidth) + x] = curPiece.getShape();
		}

		removeFullLines();

		if (!isFallingFinished)
			newPiece();
	}

	private void removeFullLines() {
		int numFullLines = 0;

		for (int i = BoardHeight - 1; i >= 0; --i) {
			boolean lineIsFull = true;

			for (int j = 0; j < BoardWidth; ++j) {
				if (shapeAt(j, i) == Tetrominoes.NoShape) {
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) {
				++numFullLines;
				for (int k = i; k < BoardHeight - 1; ++k) {
					for (int j = 0; j < BoardWidth; ++j)
						board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
				}
			}
		}

		if (numFullLines > 0) {
			numLinesRemoved += numFullLines;
			score=score+numFullLines*level*scoringFactor;
			if(numLinesRemoved>rowsForLevel) {
				updateLevel();

			}
			lines=numLinesRemoved;
			statusbar.setText(String.valueOf(numLinesRemoved));
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NoShape);
			repaint();
		}
	}

	private void updateLevel() {
		numLinesRemoved =0;
		level=level+1;
		fallingSpeed= fallingSpeed*((1+(float)level*speedFactor));
		timerDuration=1/fallingSpeed;
	}

	private void dropDown() {
		int newY = curY;
		while (newY > 0) {
			if (!tryMove(curPiece, curX, newY - 1))
				break;
			--newY;
		}
		pieceDropped();
	}
	/**
	 * 
	 * @param x
	 * @return
	 */
	int iX(float x) {
		return Math.round(x);
	}

	int iY(float y) {
		return maxY - Math.round(y);
	}

	float fx(int x) {
		return (float) x;
	}

	float fy(int y) {
		return (float) (maxY - y);
	}
	int pixelSize() {
		return (int)Math.min(mainAreaWidth/BoardWidth, mainAreaHeight/BoardHeight);
	}
	int squareWidth() {
		return pixelSize();
	}

	int squareHeight() {
		return pixelSize();
	}

	Tetrominoes shapeAt(int x, int y) {
		return board[(y * BoardWidth) + x];
	}

	Tetrominoes nextBoardShapeAt(int x, int y) {
		return nextBoard[(y * NextBoardWidth) + x];
	}

	/**
	 * @param g
	 */
	public void drawLayout(Graphics g) {
		float side = 0.95F * minMaxXY, sideHalf = 0.5F * side, h = sideHalf * (float) Math.sqrt(3), xD, yD, xB, yB, xC,
				yC, xA, yA, xE, yE, xA1, yA1, xB1, yB1, xC1, yC1, xD1, yD1, p, q, xMainAreaCenter, yMainAreaCenter,
				fontSize, xQuitCenter, yQuitCenter, xNextShapeCenter, yNextShapeCenter, xScoreCenter, yScoreCenter;
		float wPause = sideHalf / 2, hPause = side / 8, wQuit = wPause, hQuit = hPause;


		q = 0.5F;
		p = 1 - q;

		xMainAreaCenter = xCenter - sideHalf +BoardWidth * squareWidth()/2;
		yMainAreaCenter = yCenter+sideHalf-BoardHeight*squareHeight()/2;

		xNextShapeCenter = xCenter + sideHalf / 2;

		yNextShapeCenter = yCenter + side / 3;
		// 3section for side
		xScoreCenter = xNextShapeCenter;
		yScoreCenter = yCenter;
		xQuitCenter = xNextShapeCenter;
		yQuitCenter = yCenter - side / 3;



		xD = xCenter - sideHalf;
		yD = yCenter - sideHalf;
		xB = xCenter + sideHalf;
		yB = yD;
		xC = xB;
		yC = yCenter + sideHalf;
		xA = xD;
		yA = yC;
		xE = xCenter + 0.1F * sideHalf;// - .95F * sideHalf;
		yE = yCenter + 0.90F * sideHalf;// - .95F * sideHalf/2;
		g.drawRect(iX(xA), iY(yA), BoardWidth * squareWidth(), BoardHeight * squareHeight());
		mainArea = new Rectangle(iX(xA), iY(yA), BoardWidth * squareWidth(), BoardHeight * squareHeight());


		mainAreaRectangle[0]=new Point2D(xA,yA);
		mainAreaRectangle[1]=new Point2D(xA+BoardWidth * squareWidth(),yA);
		mainAreaRectangle[2]=new Point2D(xA+BoardWidth * squareWidth(),yA-BoardHeight * squareHeight());
		mainAreaRectangle[3]=new Point2D(xA,yA-BoardHeight * squareHeight());





		g.drawRect(iX(xNextShapeCenter - 0.8F * sideHalf / 2), iY(yNextShapeCenter + 0.8F * side / 6),
				iX(0.80F * sideHalf), iX(0.8F * side / 3));
		g.setFont(new Font("SansSerif",Font.PLAIN, (int) (side/40)));
		g.drawString("Scoring Facotor- "+scoringFactor+ "   Rows For Level- "+rowsForLevel+"   SpeedFactor- "+speedFactor, iX(xScoreCenter - 3 * sideHalf / 8), iY(yScoreCenter + 3*side / 18 - side /64));

		fontSize = side / 16;
		g.setFont(new Font("SansSerif", Font.BOLD, (int) (fontSize)));
		g.drawString("Level:   "+ level, iX(xScoreCenter - 3 * sideHalf / 8), iY(yScoreCenter + side / 9 - fontSize / 2));
		g.drawString("Lines:   "+ lines, iX(xScoreCenter - 3 * sideHalf / 8), iY(yScoreCenter - fontSize / 2));
		g.drawString("Score:   "+ score, iX(xScoreCenter - 3 * sideHalf / 8), iY(yScoreCenter - side / 9 - fontSize / 2));

		if (pauseButton) {
			g.drawRect(iX(xMainAreaCenter - wPause / 2), iY(yMainAreaCenter + hPause / 2), iX(wPause), iX(hPause));
			g.drawString("Pause ", iX(xMainAreaCenter - 3 * wPause / 8), iY(yMainAreaCenter - fontSize / 2));
		}

		g.drawRect(iX(xQuitCenter - wQuit / 2), iY(yQuitCenter + hQuit / 2), iX(wQuit), iX(hQuit));
		quitArea = new Rectangle(iX(xQuitCenter - wQuit / 2), iY(yQuitCenter + hQuit / 2), iX(wQuit), iX(hQuit));
		g.drawString("Quit", iX(xQuitCenter - 3 * wQuit / 8), iY(yQuitCenter - fontSize / 2));

	}


	public void paint(Graphics g) {
		super.paint(g);
		initgr();

		float side = 0.95F * minMaxXY, sideHalf = 0.5F * side, xM, yM;
		yM = yCenter + sideHalf;
		xM = xCenter - sideHalf;
		float xNextShapeCenter = xCenter + sideHalf / 2;

		float yNextShapeCenter = yCenter + side / 3;
		int xN = (int) (xNextShapeCenter - 0.8F * sideHalf / 2);
		int yN = (int) (yNextShapeCenter + 0.8F * side / 6);

		int boardTop = iY(yM);
		int nextBoardTop = iY(yN);
		// int nextboardTop=
		mainArea = new Rectangle(iX(xM), iY(yM), BoardWidth * squareWidth(), BoardHeight * squareHeight());

		for (int i = 0; i < NextBoardHeight; ++i) {
			for (int j = 0; j < NextBoardWidth; ++j) {
				Tetrominoes shape = nextBoardShapeAt(j, NextBoardHeight - i - 1);
				if (shape != Tetrominoes.NoShape)
					drawSquare(g, iX(xN) + j * squareWidth(), nextBoardTop + i * squareHeight(), shape);
			}
		}

		for (int i = 0; i < BoardHeight; ++i) {
			for (int j = 0; j < BoardWidth; ++j) {
				Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
				if (shape != Tetrominoes.NoShape)
					drawSquare(g, iX(xM) + j * squareWidth(), boardTop + i * squareHeight(), shape);
			}
		}

		if (curPiece.getShape() != Tetrominoes.NoShape) {
			for (int i = 0; i < 4; ++i) {
				int x = curX + curPiece.x(i);
				int y = curY - curPiece.y(i);
				drawSquare(g, iX(xM) + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
						curPiece.getShape());
			}
		}

		drawLayout(g);

	}

	private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
		Color colors[] = { new Color(0, 0, 0), new Color(255, 255, 0), new Color(112, 48, 160),
				new Color(0, 176, 240), new Color(255, 192, 0), new Color(0, 176, 80), new Color(255, 0, 0),
				new Color(0, 112, 192),new Color(165, 165, 165),new Color(146, 208, 80), 
				new Color(217, 149, 148) ,new Color(227, 108, 10) ,new Color(194, 214, 155), 
				new Color(147, 137, 83) ,new Color(23, 54, 93) ,new Color(49, 132, 155) };

		Color color = colors[shape.ordinal()];

		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		g.setColor(colors[0]);
		g.drawLine(x, y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);

		g.setColor(colors[0]);
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
	}

	class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {

			if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
				return;
			}

			int keycode = e.getKeyCode();

			if (keycode == 'p' || keycode == 'P') {
				// pause();
				return;
			}

			if (isPaused)
				return;

			switch (keycode) {
			case KeyEvent.VK_LEFT:
				tryMove(curPiece, curX - 1, curY);
				break;
			case KeyEvent.VK_RIGHT:
				tryMove(curPiece, curX + 1, curY);
				break;
			case KeyEvent.VK_DOWN:
				tryMove(curPiece.rotateRight(), curX, curY);
				break;
			case KeyEvent.VK_UP:
				tryMove(curPiece.rotateLeft(), curX, curY);
				break;
			case KeyEvent.VK_SPACE:
				dropDown();
				break;
			case 'd':
				oneLineDown();
				break;
			case 'D':
				oneLineDown();
				break;
			}

		}
	}

}
