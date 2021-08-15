package com.example.wiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import com.example.wiki.data.dataRepository;

@SpringBootApplication
public class WikiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WikiApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(dataRepository repo) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Set<String> bots = new HashSet<>();
                Set<String> admins = new HashSet<>();
                loadUserType("bots.txt", bots);
                loadUserType("administrators.txt", admins);

                List<dataEntry> userWithNoUserType = repo.findByUsertypeIsNull();
                System.out.println("num of users without usertype: "+userWithNoUserType.size());
                if(userWithNoUserType.size()>0) {
                    for (dataEntry uwnut : userWithNoUserType) {
                        if (admins.contains(uwnut.getUser())) {
                            uwnut.setUsertype("admin");
                        }

                        if (bots.contains(uwnut.getUser())) {
                            uwnut.setUsertype("bot");
                        }

                        repo.save(uwnut);
                    }

                    List<dataEntry> userWithAnon = repo.findByAnonNotNull();
                    for (dataEntry uwa : userWithAnon) {
                        uwa.setUsertype("anon");
                        repo.save(uwa);
                    }

                    List<dataEntry> userWithNoUserTypeAndNoAnon = repo.findByUsertypeIsNullAndAnonIsNull();
                    for (dataEntry de : userWithNoUserTypeAndNoAnon) {
                        de.setUsertype("regular");
                        repo.save(de);
                    }
                }
                System.out.println("done!");
            }
        };
    }

    public void loadUserType(String path, Set<String> userGroup){
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                userGroup.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
