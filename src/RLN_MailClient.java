import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RLN_MailClient extends JFrame {

    private JTextField portField;
    private JButton connectButton;

    public RLN_MailClient() {
        super("MailClient");

        // Initialize components
        portField = new JTextField(10);
        connectButton = new JButton("Connect");

        // Set layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter the MailServer's Port:"));
        inputPanel.add(portField);
        inputPanel.add(connectButton);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Add listeners
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        // Set up the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(null); // Center the frame
        setVisible(true);
    }

    private void connectToServer() {
        String port = portField.getText();
        try {
            InetAddress ip = InetAddress.getLocalHost();
            Socket socket = new Socket(ip, Integer.parseInt(port));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            popup("Welcome to RLN Mail Service!! :)");
            guestSession(socket, in, out);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Couldn't connect to server due to a fatal error. Please check connection to server and try again.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("A fatal error occurred.");
            System.exit(1);
        }
    }

    private static void popup(String message) {
        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void guestSession(Socket socket, BufferedReader dis, PrintWriter dos) {
        JFrame frame = new JFrame("|| Login Page ||");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Center the frame

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea messageArea = new JTextArea("Welcome to RLN Mail Service!!.\n");
        messageArea.setEditable(false);
        panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JButton signInButton = new JButton("Signup");
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);
        inputPanel.add(signInButton);
        inputPanel.add(loginButton);
        inputPanel.add(new JLabel()); // Empty label for spacing
        inputPanel.add(exitButton);

        panel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.println("signin");
                    dos.println(usernameField.getText());
                    dos.println(passwordField.getText());
                    String response = dis.readLine();
                    if (response.equalsIgnoreCase("ok")) {
                        messageArea.append("User " + usernameField.getText() + " registered successfully !!\n");
                        usernameField.setText("");
                        passwordField.setText("");
                    } else {
                        messageArea.append("Registration failed.\n");
                    }
                } catch (IOException ex) {
                    System.out.println("A fatal error occurred.");
                    System.exit(1);
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.println("login");
                    dos.println(usernameField.getText());
                    dos.println(passwordField.getText());
                    String response = dis.readLine();
                    if (response.equalsIgnoreCase("ok")) {
                        userSession(usernameField.getText(), socket, dis, dos);
                        usernameField.setText("");
                        passwordField.setText("");
                        messageArea.setText("");
                        messageArea.append("Welcome Again to RLN Mail Service !!\n");
                    } else {
                        messageArea.append("Login failed. Please check your credentials.\n");
                    }
                } catch (IOException ex) {
                    System.out.println("A fatal error occurred.");
                    System.exit(1);
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit(socket, dos);
                frame.dispose(); // Close the frame
            }
        });
    }

    private static void exit(Socket socket, PrintWriter dos) {
        try {
            dos.println("exit");
            socket.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Couldn't terminate connection to server properly.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(null, "Thanks for using RLN Mail Service!! :D", "Exit", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private static void userSession(String username, Socket socket, BufferedReader dis, PrintWriter dos) {
        JFrame frame = new JFrame("Welcome to " + username + "'s Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // Center the frame

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea messageArea = new JTextArea("Hello , " + username + ",\nYou can manage your emails here.\n");
        messageArea.setEditable(false);
        panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField receiverField = new JTextField();
        JTextField subjectField = new JTextField();
        JTextArea mainBodyArea = new JTextArea();
        JButton newEmailButton = new JButton("Compose New Email");
        JButton showEmailsButton = new JButton("Show All Emails");
        JButton readEmailButton = new JButton("Read Email");
        JButton deleteEmailButton = new JButton("Delete Email");
        JButton logoutButton = new JButton("Logout");
        JButton exitButton = new JButton("Exit");

        inputPanel.add(new JLabel("To:"));
        inputPanel.add(receiverField);
        inputPanel.add(new JLabel("Subject:"));
        inputPanel.add(subjectField);
        inputPanel.add(new JLabel("Main Body:"));
        inputPanel.add(new JScrollPane(mainBodyArea));
        inputPanel.add(newEmailButton);
        inputPanel.add(showEmailsButton);
        inputPanel.add(readEmailButton);
        inputPanel.add(deleteEmailButton);
        inputPanel.add(logoutButton);
        inputPanel.add(exitButton);

        panel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        newEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.println("newemail");
                    dos.println(receiverField.getText());
                    dos.println(subjectField.getText());
                    dos.println(mainBodyArea.getText());
                    String response = dis.readLine();
                    if (response.equalsIgnoreCase("ok")) {
                        messageArea.append("Email was sent successfully!\n");
                        receiverField.setText("");
                        subjectField.setText("");
                        mainBodyArea.setText("");
                    } else {
                        messageArea.append("There was an error while sending the email. Please try again.\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(); // Handle the exception properly
                }
            }
        });

        showEmailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.println("showemails");
                    String response = dis.readLine();
                    if (response.isEmpty()) {
                        messageArea.append("\nThere are no emails yet.\n");
                    } else {
                        messageArea.append(response + "\n");
                    }
                } catch (IOException ex) {
                    System.out.println("A fatal error occurred.");
                    System.exit(1);
                }
            }
        });

        readEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.println("reademail");
                    String index = JOptionPane.showInputDialog("Enter the email ID to be read:");
                    dos.println(index);
                    String response = dis.readLine();
                    if (!response.isEmpty()) {
                        messageArea.append(response + "\n");
                    } else {
                        messageArea.append("There is no email with ID " + index + "\n");
                    }
                } catch (IOException ex) {
                    System.out.println("A fatal error occurred.");
                    System.exit(1);
                }
            }
        });

        deleteEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.println("deleteemail");
                    String index = JOptionPane.showInputDialog("Enter the email ID to be deleted:");
                    dos.println(index);
                    String response = dis.readLine();
                    if (response.equalsIgnoreCase("ok")) {
                        messageArea.append("Email deleted successfully!\n");
                    } else {
                        messageArea.append("There is no email with ID " + index + "\n");
                    }
                } catch (IOException ex) {
                    System.out.println("A fatal error occurred.");
                    System.exit(1);
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dos.println("logout");
                frame.dispose(); // Close the user session window
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit(socket, dos);
            }
        });

    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RLN_MailClient();
            }
        });
    }
}
