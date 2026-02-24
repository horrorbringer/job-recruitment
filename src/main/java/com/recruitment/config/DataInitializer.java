package com.recruitment.config;

import com.recruitment.model.*;
import com.recruitment.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final RecruiterRepository recruiterRepository;
    private final CategoryRepository categoryRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            initializeData();
        } else if (jobRepository.count() == 0) {
            seedExistingData();
        }
    }

    private void seedExistingData() {
        log.info("Seeding jobs for existing data...");
        
        var recruiterUserOpt = userRepository.findByEmail("recruiter@company.com");
        if (recruiterUserOpt.isEmpty()) return;
        
        var recruiterOpt = recruiterRepository.findByUserId(recruiterUserOpt.get().getId());
        if (recruiterOpt.isEmpty()) return;
        
        var recruiter = recruiterOpt.get();
        var categories = categoryRepository.findAll();
        seedJobs(recruiter, categories);
        
        log.info("Jobs seeded successfully!");
    }

    private void initializeData() {
        log.info("Initializing default data...");

        User admin = new User("admin@jobportal.com", passwordEncoder.encode("admin123"), User.Role.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        String[] categories = {
            "Information Technology", "Marketing", "Finance", "Healthcare",
            "Education", "Engineering", "Sales", "Design", "Human Resources", "Legal"
        };
        
        List<Category> savedCategories = new java.util.ArrayList<>();
        for (String cat : categories) {
            Category category = new Category(cat);
            savedCategories.add(categoryRepository.save(category));
        }

        User recruiterUser = new User("recruiter@company.com", passwordEncoder.encode("recruiter123"), User.Role.RECRUITER);
        recruiterUser.setEnabled(true);
        userRepository.save(recruiterUser);

        Recruiter recruiter = new Recruiter();
        recruiter.setUser(recruiterUser);
        recruiter.setCompanyName("Tech Corp Inc.");
        recruiter.setCompanyDescription("Leading technology solutions provider");
        recruiter.setCompanyWebsite("https://techcorp.com");
        recruiter.setCompanyLocation("New York, NY");
        recruiter.setVerified(true);
        recruiterRepository.save(recruiter);

        User seekerUser = new User("jobseeker@email.com", passwordEncoder.encode("seeker123"), User.Role.JOB_SEEKER);
        seekerUser.setEnabled(true);
        userRepository.save(seekerUser);

        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setUser(seekerUser);
        jobSeeker.setFullName("John Doe");
        jobSeeker.setPhone("+1234567890");
        jobSeeker.setSkills("Java, Spring Boot, PostgreSQL, React");
        jobSeeker.setEducation("Bachelor's in Computer Science");
        jobSeeker.setLocation("New York, NY");
        jobSeeker.setExperienceYears(5);
        jobSeeker.setExperienceLevel(JobSeeker.ExperienceLevel.MID_LEVEL);
        jobSeekerRepository.save(jobSeeker);

        seedJobs(recruiter, savedCategories);

        log.info("Default data initialized successfully!");
    }

    private void seedJobs(Recruiter recruiter, List<Category> categories) {
        String[][] jobs = {
            {"Senior Java Developer", "We are looking for an experienced Java developer to join our team.", 
             "5+ years experience in Java, Spring Boot, PostgreSQL", "New York, NY", "120000", "150000", "FULL_TIME", "SENIOR", "Information Technology"},
            {"Frontend React Developer", "Join our dynamic frontend team to build amazing user interfaces.",
             "3+ years React experience, TypeScript, CSS", "Remote", "80000", "120000", "REMOTE", "MID_LEVEL", "Information Technology"},
            {"Marketing Manager", "Lead our marketing initiatives and grow our brand presence.",
             "5+ years marketing experience, SEO, PPC", "Los Angeles, CA", "90000", "130000", "FULL_TIME", "SENIOR", "Marketing"},
            {"Financial Analyst", "Analyze financial data and provide insights for business decisions.",
             "CFA preferred, 3+ years experience", "Chicago, IL", "70000", "100000", "FULL_TIME", "MID_LEVEL", "Finance"},
            {"UX Designer", "Create beautiful and intuitive user experiences for our products.",
             "Portfolio required, Figma, UI/UX", "San Francisco, CA", "90000", "140000", "FULL_TIME", "MID_LEVEL", "Design"},
            {"Data Scientist", "Build ML models and analyze complex datasets.",
             "Python, ML, Statistics, SQL", "Austin, TX", "110000", "160000", "FULL_TIME", "SENIOR", "Information Technology"},
            {"Sales Representative", "Drive sales growth and maintain client relationships.",
             "B2B sales, communication skills", "Miami, FL", "50000", "80000", "FULL_TIME", "ENTRY_LEVEL", "Sales"},
            {"HR Coordinator", "Support HR operations and employee engagement initiatives.",
             "HR degree, 2+ years experience", "Seattle, WA", "55000", "75000", "FULL_TIME", "ENTRY_LEVEL", "Human Resources"},
            {"DevOps Engineer", "Manage CI/CD pipelines and cloud infrastructure.",
             "AWS, Docker, Kubernetes", "Remote", "100000", "140000", "REMOTE", "SENIOR", "Information Technology"},
            {"Product Manager", "Lead product development and strategy.",
             "5+ years PM experience, Agile", "Boston, MA", "110000", "150000", "FULL_TIME", "SENIOR", "Engineering"}
        };

        for (String[] jobData : jobs) {
            Job job = new Job();
            job.setRecruiter(recruiter);
            job.setTitle(jobData[0]);
            job.setDescription(jobData[1]);
            job.setRequirements(jobData[2]);
            job.setLocation(jobData[3]);
            job.setSalaryMin(new BigDecimal(jobData[4]));
            job.setSalaryMax(new BigDecimal(jobData[5]));
            job.setJobType(Job.JobType.valueOf(jobData[6]));
            job.setExperienceLevel(Job.ExperienceLevel.valueOf(jobData[7]));
            job.setStatus(Job.Status.APPROVED);
            job.setVacancies((int) (Math.random() * 5) + 1);
            job.setDeadline(LocalDate.now().plusDays((long) (Math.random() * 30) + 15));
            
            for (Category cat : categories) {
                if (cat.getName().equals(jobData[8])) {
                    job.setCategory(cat);
                    break;
                }
            }
            
            jobRepository.save(job);
        }
    }
}
