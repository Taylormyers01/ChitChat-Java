import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class SocketClient extends JFrame implements ActionListener, Runnable {
    JTextArea textArea = new JTextArea(); //Scrollable text area
    //https://docs.oracle.com/javase/8/docs/api/javax/swing/JTextArea.html
    //better explanation with pics https://www.javatpoint.com/java-jtextarea
    JScrollPane jp = new JScrollPane(textArea);
    //https://www.javatpoint.com/java-jscrollpane
    JTextField input_Text = new JTextField();
    //https://www.javatpoint.com/java-jtextfield
    //similar to a text field box you fill in
    JMenuBar menuBar = new JMenuBar();
    Logger logger = Logger.getLogger("ChitChat.log");
    FileHandler fh = new FileHandler("/Users/taylor/LocalProjects/ChitChat-Java/ChitChat.log");//, true
    Socket sk;
    BufferedReader br;
    PrintWriter pw;

    public SocketClient() throws SecurityException, IOException {
        super("Chit Chat"); //created JFrame named ChitChat
        setFont(new Font("Arial Black", Font.PLAIN, 12));
        setForeground(new Color(0, 0, 51));
        setBackground(new Color(51, 0, 0));
        //this is the window creation - still not visable

        logger.addHandler(fh);
        textArea.setToolTipText("Chat History");
        textArea.setForeground(new Color(50, 205, 50));
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.BOLD, 13));

        textArea.setBackground(new Color(0, 0, 0));

/*
         JMenu helpMenu = new JMenu("Help");
        JMenuItem update = new JMenuItem("Update Information");
        JMenuItem connect_List = new JMenuItem("Visitor List");

        helpMenu.add(update);
        helpMenu.add(connect_List);

        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
*/
        getContentPane().add(jp, "Center"); //apparently has to do with layered panes in Swing?
        //https://stackoverflow.com/questions/16744152/java-gui-about-getcontentpane-method-and-content#:~:text=In%20Java%20Swing%2C%20the%20layer,the%20Java%20run%20time%20environment.
        //
        input_Text.setText(" "); // Enter your Message: // changed this since I didnt like it adding the literal Enter your message
        input_Text.setToolTipText("Enter your Message");
        input_Text.setForeground(new Color(0, 0, 0));
        input_Text.setFont(new Font("Tahoma", Font.BOLD, 11));
        input_Text.setBackground(new Color(230, 230, 250));
        
        getContentPane().add(input_Text, "South");
        setSize(325, 411);
        setVisible(true);

        input_Text.requestFocus(); //Place cursor at run time, work after screen is shown

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        input_Text.addActionListener(this); //Event registration
    }

    public void serverConnection() {
        try {
            String IP = JOptionPane.showInputDialog(this, "Please enter a server IP.", JOptionPane.INFORMATION_MESSAGE);
            sk = new Socket(IP, 1234);

            String name = JOptionPane.showInputDialog(this, "Please enter a nickname", JOptionPane.INFORMATION_MESSAGE);
/*            while (name.length() > 7) {
                name = JOptionPane.showInputDialog(this, "Please enter a nickname.(7 characters or less)", JOptionPane.INFORMATION_MESSAGE);
            }
*/          logger.info(name + " has joined the server!");


            //read
            br = new BufferedReader(new InputStreamReader(sk.getInputStream()));

            //writing
            pw = new PrintWriter(sk.getOutputStream(), true);
            pw.println(name); // Send to server side

            new Thread(this).start();

        } catch (Exception e) {
            System.out.println(e + " Socket Connection error");
        }
    }

    public static void main(String[] args) throws IOException {
        new SocketClient().serverConnection(); //Method call at the same time object creation
    }

    @Override
    public void run() {
        String data = null;
        try {
            while ((data = br.readLine()) != null) {
                textArea.append(data + "\n"); //textArea Decrease the position of the box's scroll bar by the length of the text entered
                textArea.setCaretPosition(textArea.getText().length());
            }
        } catch (Exception e) {
            System.out.println(e + "--> Client run fail");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String data = input_Text.getText();
        pw.println(data); // Send to server side
        logger.info(data + " :Chat message");
        input_Text.setText("");
    }
}