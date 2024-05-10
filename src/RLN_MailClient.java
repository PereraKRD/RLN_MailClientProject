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
        inputPanel.add(new JLabel("MailServer's Port:"));
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
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            popup("Welcome to MailServer!");
            // Pass the socket, input stream, and output stream to the guest session method
            guestSession(socket, dis, dos);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Couldn't connect to server due to a fatal error. Please check connection to server and try again.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    private static void popup(String message) {
        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void guestSession(Socket socket, DataInputStream dis, DataOutputStream dos) {
        JFrame frame = new JFrame("Guest Session");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Center the frame

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea messageArea = new JTextArea("You are connected as guest.\n");
        messageArea.setEditable(false);
        panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JButton signInButton = new JButton("Sign In");
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        inputPanel.add(new JLabel("Username:"));
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
                    dos.writeUTF("signin");
                    dos.writeUTF(usernameField.getText());
                    dos.writeUTF(passwordField.getText());
                    String response = dis.readUTF();
                    if (response.equalsIgnoreCase("ok")) {
                        messageArea.append("User " + usernameField.getText() + " was successfully registered!\n");
                    } else {
                        messageArea.append("Registration failed.\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(); // Handle the exception properly
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.writeUTF("login");
                    dos.writeUTF(usernameField.getText());
                    dos.writeUTF(passwordField.getText());
                    String response = dis.readUTF();
                    if (response.equalsIgnoreCase("ok")) {
                        userSession(usernameField.getText(), socket, dis, dos);
                        messageArea.append("You are connected as guest.\n");
                    } else {
                        messageArea.append("Login failed. Please make sure you entered the right credentials.\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(); // Handle the exception properly
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

    private static void exit(Socket socket, DataOutputStream dos) {
        try {
            dos.writeUTF("exit");
            socket.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Couldn't terminate connection to server properly.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(null, "Thanks for using MailServer :D", "Exit", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private static void userSession(String username, Socket socket, DataInputStream dis, DataOutputStream dos) {
        JFrame frame = new JFrame("User Session (" + username + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // Center the frame

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea messageArea = new JTextArea("You are connected as " + username + ".\n");
        messageArea.setEditable(false);
        panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField receiverField = new JTextField();
        JTextField subjectField = new JTextField();
        JTextArea mainBodyArea = new JTextArea();
        JButton newEmailButton = new JButton("New Email");
        JButton showEmailsButton = new JButton("Show Emails");
        JButton readEmailButton = new JButton("Read Email");
        JButton deleteEmailButton = new JButton("Delete Email");
        JButton logoutButton = new JButton("Logout");
        JButton exitButton = new JButton("Exit");

        inputPanel.add(new JLabel("Receiver:"));
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
                    dos.writeUTF("newemail");
                    dos.writeUTF(receiverField.getText());
                    dos.writeUTF(subjectField.getText());
                    dos.writeUTF(mainBodyArea.getText());
                    String response = dis.readUTF();
                    if (response.equalsIgnoreCase("ok")) {
                        messageArea.append("Email was sent successfully!\n");
                    } else {
                        messageArea.append("Couldn't send email.\n");
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
                    dos.writeUTF("showemails");
                    String response = dis.readUTF();
                    if (response.isEmpty()) {
                        messageArea.append("There are no emails yet.\n");
                    } else {
                        messageArea.append(response + "\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(); // Handle the exception properly
                }
            }
        });

        readEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.writeUTF("reademail");
                    String index = JOptionPane.showInputDialog("Enter the email ID to be read:");
                    dos.writeUTF(index);
                    String response = dis.readUTF();
                    if (!response.isEmpty()) {
                        messageArea.append(response + "\n");
                    } else {
                        messageArea.append("Email #" + index + " doesn't exist.\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(); // Handle the exception properly
                }
            }
        });

        deleteEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.writeUTF("deleteemail");
                    String index = JOptionPane.showInputDialog("Enter the email ID to be deleted:");
                    dos.writeUTF(index);
                    String response = dis.readUTF();
                    if (response.equalsIgnoreCase("ok")) {
                        messageArea.append("Email #" + index + " was deleted successfully!\n");
                    } else {
                        messageArea.append("Email #" + index + " doesn't exist.\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(); // Handle the exception properly
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.writeUTF("logout");
                } catch (IOException ex) {
                    ex.printStackTrace(); // Handle the exception properly
                }
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
