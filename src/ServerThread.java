import java.io.*;


class ServerThread extends Thread{
    private static int clientCount = 0;
    final private int clientId;
    final private RLN_MailServer server;
    final private DataOutputStream dos;
    final private DataInputStream dis;

    ServerThread(RLN_MailServer server, DataInputStream dis, DataOutputStream dos){
        this.server = server;
        this.dis = dis;
        this.dos = dos;
        clientId = clientCount++;
    }

    @Override
    public void run(){
        System.out.println("Client " + clientId + " connected.");
        try {
            String request;
            String response;
            boolean connected = true;

            while(connected){

                request = dis.readUTF();

                // Exit
                if(request.equalsIgnoreCase("exit")){
                    connected = false;
                    System.out.println("Client " + clientId + " disconnected.");
                }

                // Log-In
                else if(request.equalsIgnoreCase("login")){
                    // Get data
                    String username = dis.readUTF();
                    String password = dis.readUTF();

                    // Log user in
                    if(server.login(username, password)){
                        // Accept client
                        dos.writeUTF("ok");
                        boolean logged = true;

                        // Logged-In Session
                        while(logged){

                            // Fetch request
                            request = dis.readUTF();

                            // New Email
                            if(request.equalsIgnoreCase("newemail")){

                                // Get Data
                                String receiver = dis.readUTF();
                                String subject = dis.readUTF();
                                String mainbody = dis.readUTF();

                                // Send Email
                                if(server.newEmail(username, receiver, subject, mainbody)){
                                    response = "ok";
                                } else {
                                    response = "nok";
                                }

                                // Inform User
                                dos.writeUTF(response);
                            }

                            // Represent Emails
                            else if(request.equalsIgnoreCase("showemails")){
                                response = server.showEmails(username);
                                dos.writeUTF(response);
                            }

                            // Read Email
                            else if(request.equalsIgnoreCase("reademail")){
                                // Fetch index
                                request = dis.readUTF();

                                // Retrieve e-mail
                                response = server.readEmail(username, Integer.parseInt(request));

                                // Return e-mail
                                dos.writeUTF(response);
                            }

                            // Delete Email
                            else if(request.equalsIgnoreCase("deleteemail")){
                                // Fetch of e-mail
                                request = dis.readUTF();

                                // Retrieve e-mail
                                if(server.deleteEmail(username, Integer.parseInt(request))){
                                    response = "ok";
                                } else {
                                    response = "nok";
                                }

                                dos.writeUTF(response);
                            }

                            // Log Out
                            else if(request.equalsIgnoreCase("logout")){
                                logged = false;
                            }

                            // Exit
                            else if(request.equalsIgnoreCase("exit")){
                                logged = connected = false;
                                System.out.println("Client " + clientId + " disconnected.");
                            }
                        }
                    } else {
                        dos.writeUTF("nok");
                    }
                }

                // Sign-In
                else if(request.equalsIgnoreCase("signin")){
                    // Get data
                    String username = dis.readUTF();
                    String password = dis.readUTF();

                    // Register user
                    if(server.register(username, password)){
                        response = "ok";
                    } else {
                        response = "nok";
                    }

                    // Inform client
                    dos.writeUTF(response);
                }
            }

        } catch (IOException e){
            System.out.println("An error has occurred while communicating with a client.");
            System.out.println("Client " + clientId + " lost connection.");
        }
    }
}