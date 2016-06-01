package Management;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    
    
    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        String uri="mongodb://connector:MG2-E5g-uGY-sg6@ds011452.mlab.com:11452/fdepot?authMechanism=SCRAM-SHA-1";
        MongoClientURI mongoClientURI=new MongoClientURI(uri);
        MongoClient mongoClient=new MongoClient(mongoClientURI);
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(
                mongoClient, "fdepot");
        return simpleMongoDbFactory;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
    	
        return new MongoTemplate(mongoDbFactory());
    }

}
