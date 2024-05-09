public class Email {

    private final String sender;
    private final String receiver;
    private final String subject;
    private final String mainBody;
    private  boolean UnRead;


    public Email(String sender, String receiver, String subject, String mainBody) {
        if (sender == null || sender.isEmpty() ||
                receiver == null || receiver.isEmpty() ||
                subject == null || subject.isEmpty() ||
                mainBody == null || mainBody.isEmpty()) {
            throw new IllegalArgumentException("Sender, receiver, subject, and main body must not be null or empty.");
        }
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.mainBody = mainBody;
        this.UnRead = true;
    }


    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public boolean getUnRead() {
        return UnRead;
    }

    void Read() {
        UnRead = false;
    }

    @Override
    public String toString() {
        return  "From: " + sender + "\n"+
                "To: " + receiver + "\n"+
                "Subject: " + subject + "\n\n"+
                "Message" + mainBody + "\n\n\n";
    }
}
