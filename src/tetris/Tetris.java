package tetris;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class Tetris extends JFrame {

	JLabel statusbar;
	String imagesLocation=System.getProperty("user.dir") + "\\src\\tetris\\";
	JPanel frontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JPanel frontPanel1 = new JPanel();
	JPanel frontPanel2 = new JPanel();
	JPanel frontPanel3 = new JPanel();
	JPanel frontPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	ArrayList<Integer> excludeNewPieces = new ArrayList<Integer>();
	JCheckBox imageCheckBox[] = new JCheckBox[8];
	private int mainHeight;

	private int mainWidth;

	public Tetris() {

		statusbar = new JLabel(" 0");
		add(statusbar, BorderLayout.SOUTH);

		try {
			drawInitialLayout(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public JLabel getStatusBar() {
		return statusbar;
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

	private void setNewPieces() {
		for (int i = 0; i < 8; i++) {
			if (!imageCheckBox[i].isSelected()) {
				excludeNewPieces.add(i + 8);
			}
		}
		// TODO Auto-generated method stub

	}

	void drawInitialLayout(Tetris tetris) throws IOException {

		setUIFont(new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 30));

		frontPanel.setLayout(new BoxLayout(frontPanel, BoxLayout.Y_AXIS));

		// frontPanel1.setLayout(new BoxLayout(frontPanel1, BoxLayout.X_AXIS));
		frontPanel2.setLayout(new BoxLayout(frontPanel2, BoxLayout.X_AXIS));
		frontPanel3.setLayout(new BoxLayout(frontPanel3, BoxLayout.X_AXIS));
		frontPanel4.setLayout(new BoxLayout(frontPanel4, BoxLayout.Y_AXIS));
		frontPanel1.setMinimumSize(new Dimension(5000, 5000));

		frontPanel1.add(new JLabel("   MainArea Width"));
		// setting.add(btnUpdate);

		int[] valuesMW = { 10, 20, 30, 40 };
		DefaultComboBoxModel modelMW = new DefaultComboBoxModel();
		modelMW.setSelectedItem(valuesMW[0]);
		for (int value : valuesMW) {
			modelMW.addElement(value);
		}
		JComboBox comboBoxMW = new JComboBox(modelMW);

		frontPanel1.add(comboBoxMW);

		frontPanel1.add(new JLabel("   MainArea Height"));

		int[] valuesMH = { 10, 20, 30, 40 };
		DefaultComboBoxModel modelMH = new DefaultComboBoxModel();
		modelMH.setSelectedItem(valuesMH[1]);
		for (int value : valuesMW) {
			modelMH.addElement(value);
		}
		JComboBox comboBoxMH = new JComboBox(modelMH);

		frontPanel1.add(comboBoxMH);
		JCheckBox enlargePixel = new JCheckBox("Enlarged Pixel");

		// frontPanel1.setPreferredSize(new Dimension(500, 500));
		setSize(new Dimension(1000, 1000));
		Image picture = null;

		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				mainHeight = (int) comboBoxMH.getSelectedItem();
				mainWidth = (int) comboBoxMW.getSelectedItem();
				setNewPieces();
				Board CanvasTetris = new Board(tetris, excludeNewPieces, mainHeight, mainWidth);
				add(CanvasTetris);
				
				CanvasTetris.start();
				frontPanel.setVisible(false);
				if (enlargePixel.isSelected()) {
					setSize(2400, 1600);
				}
				

			}

		});

		frontPanel4.add(enlargePixel);

		for (int i = 0; i < 4; i++) {
			try {
				picture = ImageIO.read(new File(imagesLocation + (i + 1) + ".png"));
				picture = picture.getScaledInstance(140, 140, Image.SCALE_DEFAULT);
				JLabel picLabel = new JLabel(new ImageIcon(picture));

				imageCheckBox[i] = new JCheckBox("");
				frontPanel2.add(picLabel);
				frontPanel2.add(imageCheckBox[i]);
			} catch (IOException ex) {
				// handle exception...
			}

		}
		for (int i = 4; i < 8; i++) {
			try {
				picture = ImageIO.read(new File(imagesLocation + (i + 1) + ".png"));
				picture = picture.getScaledInstance(140, 140, Image.SCALE_DEFAULT);

				JLabel picLabel = new JLabel(new ImageIcon(picture));

				imageCheckBox[i] = new JCheckBox("");
				frontPanel3.add(picLabel);
				frontPanel3.add(imageCheckBox[i]);
			} catch (IOException ex) {
				// handle exception...
			}

		}
		JLabel title = new JLabel("Choose New Pieces");
		title.setFont(new Font("Serif", Font.PLAIN, 40));
		frontPanel.add(title);
		frontPanel1.add(startButton);

		frontPanel.add(frontPanel2);
		frontPanel.add(frontPanel3);
		frontPanel.add(frontPanel4);
		frontPanel.add(frontPanel1);

		// frontPanel4.add(startButton);

		setSize(1200, 800);
		setTitle("Tetris");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		add(frontPanel);

	}

	public static void main(String[] args) {

		Tetris game = new Tetris();
		game.setLocationRelativeTo(null);
		game.setVisible(true);

	}
}
