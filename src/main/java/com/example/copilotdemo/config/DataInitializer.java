package com.example.copilotdemo.config;

import com.example.copilotdemo.model.Department;
import com.example.copilotdemo.model.Employee;
import com.example.copilotdemo.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Configuration
public class DataInitializer {

    private final Random random = new Random();

    private final List<String> villainNames = Arrays.asList(
        "Morgoth the Destroyer", "Sauron the Deceiver", "Azog the Defiler", 
        "Gothmog the Balrog", "Shelob the Great", "Saruman the White", 
        "The Witch-king of Angmar", "Glaurung the Deceiver", "Ungoliant the Dark",
        "Durin's Bane", "Smaug the Magnificent", "The Mouth of Sauron", 
        "Bolg the Vengeful", "Gollum the Wretched"    );

    @Bean
    public CommandLineRunner initData(EmployeeRepository repository) {
        return args -> {            if (repository.count() == 0) {
                // Create a shuffled list of villain names to ensure no duplicates
                List<String> shuffledNames = new ArrayList<>(villainNames);
                Collections.shuffle(shuffledNames);
                
                for (int i = 0; i < 10; i++) {
                    Employee employee = new Employee();
                    String villainName = shuffledNames.get(i);
                    employee.setName(villainName);
                    // Create email based on the first word of the villain name
                    String emailName = villainName.split(" ")[0].toLowerCase();
                    employee.setEmail(emailName + "@mordor.com");                    employee.setDepartment(Department.values()[random.nextInt(Department.values().length)]);
                    // Evil overlords get paid well - between 100,000 and 1,000,000
                    employee.setSalary(100000.0 + random.nextDouble() * 900000.0);
                    repository.save(employee);
                }
            }
        };
    }
}
