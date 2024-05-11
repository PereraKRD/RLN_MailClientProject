import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class RLN_MailServer{

    // Hashmap stores the registered accounts
    private HashMap<String, Account> accounts;

    public RLN_MailServer() {
        super();
        accounts = new HashMap<>();
    }

    public static void main(String[] args) {

        int port = 7777;
        System.out.println("\n\n\t\t    --------## WELCOME TO RLN MAIL ##--------\t\t");
        System.out.println("\t\t=================================================\t\t \n\n");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Invalid or unavailable port");
            System.exit(1);
        }

        RLN_MailServer server = new RLN_MailServer();
        System.out.println("RLN Mail Server started on port : " + port);
        server.populateAccounts();

        Socket socket;
        while(true) {
            try {
                socket = serverSocket.accept();
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                Thread thread = new ServerThread(server, in, out);
                thread.start();

            } catch (Exception e) {
                System.out.println("A fatal error occurred.");
                System.exit(1);
            }
        }
    }

    private void populateAccounts() {

        register("ryan@rln.com", "123456");
        register("lasith@rln.com", "123456");
        register("nithil@rln.com", "123456");

        newEmail("ryan@rln.com", "lasith@rln.com", "Spam", "Hey there!\nThis is spam!\n");
        newEmail("ryan@rln.com", "admin@rln.com", "Spam", "Hey there!\nThis is spam!\n");
        newEmail("ryan@rln.com", "nithil@rln.com", "Spam", "Hey there!\nThis is spam!\n");

        newEmail("lasith@rln.com", "ryan@rln.com", "foo", "bar\n");
        newEmail("lasith@rln.com", "nithil@rln.com", "baz", "fizz\n");
        newEmail("lasith@rln.com", "admin@rln.com", "fuzz", "Lorem ipsum and stuff...\n");

        newEmail("nithil@rln.com", "lasith@rln.com", "Spam", "Hey there!\nThis is spam!\n");
        newEmail("nithil@rln.com", "admin@rln.com", "Spam", "Hey there!\nThis is spam!\n");
        newEmail("nithil@rln.com", "ryan@rln.com", "Spam", "Hey there!\nThis is spam!\n");

    }

    boolean register(String username, String password){

        if(!accounts.containsKey(username) && isValidEmail(username) && password.length() >= 6) {
            accounts.put(username.toLowerCase(), new Account(username, password));
            return true;
        }
        return false;
    }

    boolean isValidEmail(String email) {
        // Regex for email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    boolean login(String username, String password){
        username = username.toLowerCase();
        if(accounts.get(username) != null){
            return password.equals(accounts.get(username).getPassword());
        }
        return false;
    }

    


    boolean newEmail(String sender, String receiver, String subject, String mainBody){
        if(accounts.containsKey(sender) && accounts.containsKey(receiver)){
            Email email = new Email(sender, receiver, subject, mainBody);
            return accounts.get(receiver).submitEmail(email);
        }
        return false;
    }

    String showEmails(String username) {
        String str = "The username " + username + " does not exist.\n";
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
                str = "\n" + email.toStringFormat();
                email.Read();
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
}