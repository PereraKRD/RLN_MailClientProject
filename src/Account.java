import java.util.ArrayList;


public class Account {
    private String username, password;
    private ArrayList<Email> mailbox;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
        this.mailbox = new ArrayList<>();
    }

    String getUsername(){
        return username;
    }

    String getPassword(){
        return password;
    }


    boolean submitEmail(Email email){
        if(email != null){
            mailbox.add(0, email);
            return true;
        }
        return false;
    }


    Email getEmail(int index){
        Email email = null;
        if(index >= 0 && index < mailbox.size()){
            email = mailbox.get(index);
        }
        return email;
    }


    boolean deleteEmail(int index){
        if(index >= 0 && index < mailbox.size()){
            mailbox.remove(index);
            return true;
        }
        return false;
    }

    String representMailbox(){
        int i = 0;
        StringBuilder str = new StringBuilder();
        for(Email email:mailbox){

            str.append("[").append((email.getNew()?"*":" ")).append("]");
            str.append("\t");

            str.append("ID: ").append(i++);
            str.append("\t");

            str.append("from: ").append(email.getSender());
            str.append("\t");

            str.append(email.getSubject()).append("\n");
        }
        return str.toString();
    }
}
