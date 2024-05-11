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

        portField = new JTextField(10);
        connectButton = new JButton("Connect");

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter the RLN MailServer's Port:"));
        inputPanel.add(portField);
        inputPanel.add(connectButton);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

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
            popup("Welcome to RLN Mail Service!! :)");
            loginInterface(socket, dis, dos);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Couldn't connect to server due to a fatal error. Please check connection to server and try again.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("A fatal error occurred.");
            System.exit(1);
        }
    }

    private static void popup(String message) {
        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void loginInterface(Socket socket, DataInputStream in, DataOutputStream out) {
        JFrame frame = new JFrame("|| Login Page ||");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //close button
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Center the frame

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea messageArea = new JTextArea("Welcome to RLN Mail Service!!.\n");
        messageArea.setEditable(false);
        panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
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
                    out.writeUTF("signin");
                    out.writeUTF(usernameField.getText());
                    out.writeUTF(passwordField.getText());
                    String response = in.readUTF();
                    if (response.equalsIgnoreCase("ok")) {
                        messageArea.append("User " + usernameField.getText().toLowerCase() + " registered successfully !!\n");
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
                    out.writeUTF("login");
                    out.writeUTF(usernameField.getText());
                    out.writeUTF(passwordField.getText());
                    String response = in.readUTF();
                    if (response.equalsIgnoreCase("ok")) {
                        userInterface(usernameField.getText(), socket, in, out);
                        usernameField.setText("");
                        passwordField.setText("");
                        messageArea.setText("");
                        frame.dispose();
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
                exit(socket, out);
                frame.dispose();
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
        JOptionPane.showMessageDialog(null, "Thanks for using RLN Mail Service!! :D", "Exit", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private static void userInterface(String username, Socket socket, DataInputStream dis, DataOutputStream dos) {
        JFrame frame = new JFrame("Welcome to " + username.toLowerCase() + "'s Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea messageArea = new JTextArea("Hello , " + username.toLowerCase() + ",\nYou can manage your emails here.\n");
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
                    dos.writeUTF("newemail");
                    dos.writeUTF(receiverField.getText());
                    dos.writeUTF(subjectField.getText());
                    dos.writeUTF(mainBodyArea.getText());
                    String response = dis.readUTF();
                    if (response.equalsIgnoreCase("ok")) {
                        messageArea.append("Email was sent successfully!\n");
                        receiverField.setText("");
                        subjectField.setText("");
                        mainBodyArea.setText("");
                    } else {
                        messageArea.append("There was an error while sending the email. Please try again.\n");
                    }
                } catch (IOException ex) {
                    System.out.println("A fatal error occurred.");
                    System.exit(1);
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
                    dos.writeUTF("reademail");
                    String index = JOptionPane.showInputDialog("Enter the email ID to be read:");
                    dos.writeUTF(index);
                    String response = dis.readUTF();
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
                    dos.writeUTF("deleteemail");
                    String index = JOptionPane.showInputDialog("Enter the email ID to be deleted:");
                    dos.writeUTF(index);
                    String response = dis.readUTF();
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
                try {
                    dos.writeUTF("logout");
                } catch (IOException ex) {
                    System.out.println("A fatal error occurred.");
                    System.exit(1);
                }
                frame.dispose();
                loginInterface(socket, dis, dos);// Close the user session window
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
