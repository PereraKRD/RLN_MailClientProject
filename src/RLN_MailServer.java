import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;


public class RLN_MailServer{

    // Hashmap stores the registered accounts
    private HashMap<String, Account> accounts;

    public RLN_MailServer() {
        super();
        accounts = new HashMap<>();
    }

    public static void main(String[] args) throws IOException {

        int port = 7777;
        System.out.println("\n\n\t\t--------## WELCOME TO RLN MAIL ##--------\t\t");
        System.out.println("\t\t=========================================\t\t \n\n");
        ServerSocket serverSocket = new ServerSocket(port);

        RLN_MailServer server = new RLN_MailServer();
        System.out.println("RLN Mail Server started on port : " + port);
        server.populateAccounts();

        // Start listening for client connections
        Socket socket;
        while(true) {
            try {
                socket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                Thread thread = new ServerThread(server, dis, dos);
                thread.start();
            } catch (Exception e) {
                System.out.println("A fatal error occurred.");
                System.exit(1);
            }
        }
    }

    boolean register(String username, String password){
        if(!accounts.containsKey(username)) {
            accounts.put(username, new Account(username, password));
            return true;
        }
        return false;
    }

    boolean login(String username, String password){
        if(accounts.get(username) != null){
            return password.equals(accounts.get(username).getPassword());
        }
        return false;
    }

    boolean newEmail(String sender, String receiver, String subject, String mainbody){
        if(accounts.containsKey(sender) && accounts.containsKey(receiver)){
            Email email = new Email(sender, receiver, subject, mainbody);
            return accounts.get(receiver).submitEmail(email);
        }
        return false;
    }

    String showEmails(String username) {
        String str = "User " + username + " is not valid";
        if(accounts.containsKey(username)) {
            str = accounts.get(username).representMailbox();
        }
        return str;
    }

    String readEmail(String username, int id){
        String str = "";
        if(accounts.containsKey(username)){
            Email email = accounts.get(username).getEmail(id);
            if(email != null){
                str = "\n" + email.toString();
                email.makeSeen();
            }
        }
        return str;
    }

    boolean deleteEmail(String username, int id) {
        if(accounts.containsKey(username)){
            return accounts.get(username).deleteEmail(id);
        }
        return false;
    }


    private void populateAccounts() {

        register("ryan@rln.com", "1234");
        register("lasith@rln.com", "1234");
        register("nithil@rln.com", "1234");
        register("admin@rln.com", "admin");

        newEmail("ryan@rln.com", "lasith@rln.com", "Spam", "Hey there!\nThis is spam!\n");
        newEmail("ryan@rln.com", "admin@rln.com", "Spam", "Hey there!\nThis is spam!\n");
        newEmail("ryan@rln.com", "nithil@rln.com", "Spam", "Hey there!\nThis is spam!\n");

        newEmail("lasith@rln.com", "ryan@rln.com", "foo", "bar\n");
        newEmail("lasith@rln.com", "nithil@rln.com", "baz", "fizz\n");
        newEmail("lasith@rln.com", "admin@rln.com", "fuzz", "Lorem ipsum and stuff...\n");

        newEmail("nithil@rln.com", "lasith@rln.com", "Spam", "Hey there!\nThis is spam!\n");
        newEmail("nithil@rln.com", "admin@rln.com", "Spam", "Hey there!\nThis is spam!\n");
        newEmail("nithil@rln.com", "ryan@rln.com", "Spam", "Hey there!\nThis is spam!\n");

        newEmail("admin@rln.com", "lasith@rln.com", "!Important!", "I hope to pass Signals " +
                "and Systems this semester!\nI also hope to pass Digital Communications...\nThat's all folks!\n" +
                "Yours sincerely,\n Ryan Perera 4113");
        newEmail("admin@rln.com", "nithil@rln.com", "DO NOT OPEN", "Gotcha!\n:P :P :P :P\n");
        newEmail("admin@rln.com", "admin@rln.com", "Passwords", "No passwords in here, lol\n");
    }
}



