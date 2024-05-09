import java.util.ArrayList;

public class Account {

    private final String Email;
    private final String Password;
    private ArrayList<Email> inbox;


    public Account(String mail, String password) {
        if (mail.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password must not be null or empty.");
        }
        this.Email = mail;
        this.Password = password;
        this.inbox = new ArrayList<>();
    }


    public String getEmail() {
        return Email;
    }

    public String getPassword() {
        return Password;
    }

    public boolean sendEmail(Email email) {
        if (email == null){
            return false;
        } else {
            inbox.add(0,email);
            return true;
        }
    }

    public Email getEmail(int index) {
        if (index < 0 || index >= inbox.size()) {
            throw new IndexOutOfBoundsException("Index is out of bounds.");
        }
        return inbox.get(index);
    }

    public boolean deleteEmail(int index) {
        if (index >= 0 && index < inbox.size()) {
            inbox.remove(index);
            return true;
        }
        return false;
    }

    public String showInbox() {
        if (inbox.isEmpty()) {
            return "Inbox is empty.";
        }
        int i = 0;
        StringBuilder str = new StringBuilder();
        for (Email email : inbox) {
            str.append("[").append(email.getUnRead() ? "*" : "-").append("]").append("\t");
            str.append("#").append(i++).append("\t");
            str.append("from: ").append(email.getSender()).append("\t");
            str.append(email.getSubject()).append("\n");
        }
        return str.toString();
    }
}
