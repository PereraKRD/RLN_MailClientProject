import java.io.*;


class ServerThread extends Thread{
    private static int clientCount = 0;
    final private int clientId;
    final private RLN_MailServer server;
    final private PrintWriter dos;
    final private BufferedReader dis;

    ServerThread(RLN_MailServer server, BufferedReader dis, PrintWriter dos){
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

                request = dis.readLine();

                // Exit
                if(request.equalsIgnoreCase("exit")){
                    connected = false;
                    System.out.println("Client " + clientId + " disconnected.");
                }

                // Log-In
                else if(request.equalsIgnoreCase("login")){
                    // Get data
                    String username = dis.readLine();
                    String password = dis.readLine();

                    // Log user in
                    if(server.login(username, password)){
                        // Accept client
                        dos.println("ok");
                        boolean logged = true;

                        // Logged-In Session
                        while(logged){

                            // Fetch request
                            request = dis.readLine();

                            // New Email
                            if(request.equalsIgnoreCase("newemail")){

                                // Get Data
                                String receiver = dis.readLine();
                                String subject = dis.readLine();
                                String mainbody = dis.readLine();

                                // Send Email
                                if(server.newEmail(username, receiver, subject, mainbody)){
                                    response = "ok";
                                } else {
                                    response = "nok";
                                }

                                // Inform User
                                dos.println(response);
                            }

                            // Represent Emails
                            else if(request.equalsIgnoreCase("showemails")){
                                response = server.showEmails(username);
                                dos.println(response);
                            }

                            // Read Email
                            else if(request.equalsIgnoreCase("reademail")){
                                // Fetch index
                                request = dis.readLine();

                                // Retrieve e-mail
                                response = server.readEmail(username, Integer.parseInt(request));

                                // Return e-mail
                                dos.println(response);
                            }

                            // Delete Email
                            else if(request.equalsIgnoreCase("deleteemail")){
                                // Fetch of e-mail
                                request = dis.readLine();

                                // Retrieve e-mail
                                if(server.deleteEmail(username, Integer.parseInt(request))){
                                    response = "ok";
                                } else {
                                    response = "nok";
                                }

                                dos.println(response);
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
                        dos.println("nok");
                    }
                }

                // Sign-In
                else if(request.equalsIgnoreCase("signin")){
                    // Get data
                    String username = dis.readLine();
                    String password = dis.readLine();

                    // Register user
                    if(server.register(username, password)){
                        response = "ok";
                    } else {
                        response = "nok";
                    }

                    // Inform client
                    dos.println(response);
                }
            }

        } catch (IOException e){
            System.out.println("An error has occurred while communicating with a client.");
            System.out.println("Client " + clientId + " lost connection.");
        }
    }
}