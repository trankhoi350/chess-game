package main;
import javax.swing.JFrame;


public class Main 
{
    public static void main(String[] args) 
    {
        JFrame window = new JFrame("Chess Game");  //create window for the game
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false); //cannot resize the window

        GamePanel gp = new GamePanel();
        window.add(gp); //contain game panel into the window
        window.pack();

        window.setLocationRelativeTo(null); //the window will pop up at the center of the screen
        window.setVisible(true);
        gp.launchGame();
    }
}
