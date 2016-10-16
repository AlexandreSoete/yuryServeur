import java.lang.String;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        // création de la socket
        int port = 80;
        ServerSocket serverSocket = new ServerSocket(port);
        System.err.println("Serveur lancé sur le port : " + port);

        // repeatedly wait for connections, and process
        while (true) {
            // on reste bloqué sur l'attente d'une demande client
            Socket clientSocket = serverSocket.accept();
            System.err.println("Nouveau client connecté");

            // on ouvre un flux de converation

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            // chaque fois qu'une donnée est lue sur le réseau on la renvoi sur
            // le flux d'écriture.
            // la donnée lue est donc retournée exactement au même client.
            String s, file = "index.html", contentType = "text/html";
            while ((s = in.readLine()) != null) {
                System.out.println(s);
                if (s.isEmpty()) {
                    break;
                }
                String search = "GET";
                int key = s.toLowerCase().indexOf(search.toLowerCase());
                if(key != -1){
                    String[] split = s.split(" ");

                    if(split[1].length() > 1){
                        file = split[1];
                        String[] extension = split[1].split("\\.");
                        if (extension[1].equals("css")) {
                            contentType = "text/css";
                        }

                        if(extension[1].equals("jpg")){
                            contentType = "image/jpg";
                        }



                    }
                }
            }
            File f = new File("/home/yury/Documents/lab/Sites/html/"+file);
            if(f.exists()){
                Path wiki_path = Paths.get("/home/yury/Documents/lab/Sites/html/", file);
                Charset charset = Charset.forName("ISO-8859-1");
                java.util.List<String> lines = Files.readAllLines(wiki_path, charset);


                out.write("HTTP/1.0 200 OK\r\n"); // la gestion des requete n'est pas encore faite. S'il ne connait pas le fichier, il renverra quand même ok
                out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
                out.write("Server: yuryServeur/0.8.4\r\n");
                out.write("Content-Type: "+ contentType +"\r\n");
                out.write("Content-Length: 59\r\n");
                out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
                out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
                out.write("\r\n");
                for (String line : lines) {
                    out.write(line);
                }
            }

            // on ferme les flux.
            System.err.println("Connexion avec le client terminée");
            out.close();
            in.close();
            clientSocket.close();
        }
    }

}

