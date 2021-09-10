package ruslan.simakov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class SpringDataApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(SpringDataApplication.class, args);
        CriminalRepository criminalRepository = context.getBean(CriminalRepository.class);

        List<Criminal> criminals = criminalRepository.findByNumberBetween(20, 50);
        for (Criminal criminal : criminals) {
            System.out.println(criminal);
        }
    }

}
