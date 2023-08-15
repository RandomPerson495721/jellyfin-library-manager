package dev.partin.james.jellyfinlibrarymanager;

import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.sql.DataSource;

@SpringBootApplication
public class JellyfinLibraryManagerApplication {
	//TODO: Write tests
	public static void main(String[] args) {
		SpringApplication.run(JellyfinLibraryManagerApplication.class, args);
	}

}
