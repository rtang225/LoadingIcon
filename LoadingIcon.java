//Raymond T. May 9, 2022, Loading Icon Graphics
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class LoadingIcon extends JFrame implements ActionListener, KeyListener {
    public static void main(String[] args) {
        new LoadingIcon();
    }

    DrawingPanel panel;
    static int panW = 1100;
    static int panH = 800;

    int ballRed = 255, ballGreen = 255, ballBlue = 255; //ball color
    int ballSpeed = 1;
    int colorStep = ballSpeed*3;
    int gaps = 50;

    int squareLength = 100;
    int squareY = panH / 2 - squareLength / 2; //top of the square to center it in the middle of the screen

    int[][] color = { //square colors (first 3 are initial values, last 3 are values that change)
        {255, 0, 0, 255, 0, 0},
        {255, 128, 0, 255, 128, 0},
        {255, 255, 0, 255, 255, 0},
        {0, 255, 0, 0, 255, 0},
        {0, 0, 255, 0, 0, 255},
        {128, 0, 255, 128, 0, 255},
        {255, 0, 255, 255, 0, 255}
    };

    boolean[] colorChange = new boolean [7]; //changes to true when the ball hits the square
    boolean[] peak = new boolean [7]; //determines if the square is completely white

    int center = 0;
    int radius;
    int size = 19;

    int offsetX = 0;

    LoadingIcon() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setUndecorated(true);

        panel = new DrawingPanel();

        this.setContentPane(panel);
        this.setBackground(new Color(0, 0, 0, 0));
        this.pack();
        this.setVisible(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        this.addKeyListener(this);

        Timer timer = new Timer(1, this);
        timer.start();
    }

    class DrawingPanel extends JPanel {
        DrawingPanel() {
            this.setPreferredSize(new Dimension(panW, panH));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < 7; i++) {
                int squareX = (i + 1) * gaps + i * squareLength;
                if (center >= squareX && center <= squareX + squareLength && radius == 25) colorChange[i] = true;
                g.setColor(squareColor(i));
                g.fillRect(squareX, squareY, squareLength, squareLength);

                Color ballColor = new Color(ballRed, ballGreen, ballBlue);
                g.setColor(ballColor);
                Circle circle = new Circle(center, panH / 2, radius);
                g.fillOval(circle.x, circle.y, circle.width, circle.height);
                
                g.setColor(Color.BLACK);
                g.drawOval(circle.x, circle.y, circle.width, circle.height);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (center > panW) {
            center = 0;
            size = 19;
            ballRed = 225;
            ballGreen = 225;
            ballBlue = 225;
        }
        else center += ballSpeed;
        radius = (int) (75 + 50 * Math.sin(size * Math.PI / 75));
        size++;
        panel.repaint();
    }

    Color squareColor(int i) {
        for (int j = 0; j < 3; j++) {
            if (colorChange[i] && !peak[i]) //increase color
            {
                ballRed = color[i][0];
                ballGreen = color[i][1];
                ballBlue = color[i][2];
                if (color[i][j] == 128) color[i][j + 3] += colorStep / 2;
                if (color[i][j] == 0) color[i][j + 3] += colorStep;
            }
            if (color[i][j+3] > 255) color[i][j+3] = 255; //make sure it doesn't go over 255

            if (peak[i]) //decreases color
            {
                if (color[i][j] == 128) color[i][j + 3] -= colorStep / 2;
                if (color[i][j] == 0) color[i][j + 3] -= colorStep;
            }
            if (color[i][j+3] < color[i][j]) color[i][j+3] = color[i][j]; //make sure it doesn't go over lowest point
            
        }
        if (color[i][3] == 255 && color[i][4] == 255 && color[i][5] == 255) peak[i] = true; //find peak
        if (color[i][3] == color[i][0] && color[i][4] == color[i][1] && color[i][5] == color[i][2]) //cycle complete and ends loop
        {
            peak[i] = false;
            colorChange[i] = false;
        }
        Color c = new Color(color[i][3], color[i][4], color[i][5]);
        return c;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 27) System.exit(0);
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}