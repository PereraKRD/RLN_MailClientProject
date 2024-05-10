import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


class ServerThread extends Thread{
    private static int sessionCount = 0;
    final private int sid;
    final private RLN_MailServer server;
    final private DataInputStream dis;
    final private DataOutputStream dos;

    ServerThread(RLN_MailServer server, DataInputStream dis, DataOutputStream dos){
        this.server = server;
        this.dis = dis;
        this.dos = dos;
        sid = sessionCount++;
    }

    @Override
    public void run(){
        System.out.println("Connected to Session " + sid );
        try {
            String request;
            String response;
            boolean connected = true;

            while(connected){

                request = dis.readUTF();

                // Exit
                if(request.equalsIgnoreCase("exit")){
                    connected = false;
                    System.out.println("Session " + sid + " has ended.");
                }

                // Log-In
                else if(request.equalsIgnoreCase("login")){
                    // Get data
                    String username = dis.readUTF();
                    String password = dis.readUTF();


                    if(server.login(username, password)){
                        // Accept client
                        dos.writeUTF("logged in as " + username);
                        boolean logged = true;


                        while(logged){

                            request = dis.readUTF();


                            if(request.equalsIgnoreCase("newemail")){


                                String receiver = dis.readUTF();
                                String subject = dis.readUTF();
                                String mainbody = dis.readUTF();

                                // Send Email
                                if(server.newEmail(username, receiver, subject, mainbody)){
                                    response = "ok";
                                } else {
                                    response = "nok";
                                }


                                dos.writeUTF(response);
                            }

                            // Represent Emails
                            else if(request.equalsIgnoreCase("showemails")){
                                response = server.showEmails(username);
                                dos.writeUTF(response);
                            }

                            // Read Email
                            else if(request.equalsIgnoreCase("reademail")){

                                request = dis.readUTF();


                                response = server.readEmail(username, Integer.parseInt(request));


                                dos.writeUTF(response);
                            }

                            // Delete Email
                            else if(request.equalsIgnoreCase("deleteemail")){

                                request = dis.readUTF();


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
                                System.out.println("Session #" + sid + " has been terminated.");
                            }
                        }
                    } else {
                        dos.writeUTF("nok");
                    }
                }

                // Sign-In
                else if(request.equalsIgnoreCase("signin")){

                    String username = dis.readUTF();
                    String password = dis.readUTF();


                    if(server.register(username, password)){
                        response = "ok";
                    } else {
                        response = "nok";
                    }

                    dos.writeUTF(response);
                }
            }

        } catch (IOException e){
            System.out.println("An error has occurred while communicating with a client.");
            System.out.println("Session " + sid + " lost.");
        }
    }
}