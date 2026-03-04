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
        } else if (jobRepository.findByTitleContaining("Senior Full Stack Engineer").isEmpty()) {
            seedExistingData();
        }
    }

    private void seedExistingData() {
        log.info("Seeding jobs for existing data...");

        var recruiterUserOpt = userRepository.findByEmail("recruiter@company.com");
        if (recruiterUserOpt.isEmpty())
            return;

        var recruiterOpt = recruiterRepository.findByUserId(recruiterUserOpt.get().getId());
        if (recruiterOpt.isEmpty())
            return;

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

        User recruiterUser = new User("recruiter@company.com", passwordEncoder.encode("recruiter123"),
                User.Role.RECRUITER);
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
        Object[][] jobDataList = {
                {
                        "Senior Full Stack Engineer (Java/React)",
                        "We are looking for a Senior Full Stack Engineer to lead the development of our core platform. You will work with a modern tech stack and solve complex scalability challenges.",
                        "8+ years of experience in Java and Spring Boot\nStrong proficiency in React, TypeScript, and modern CSS\nExperience with microservices architecture and AWS\nExcellent problem-solving and communication skills",
                        "Design and implement scalable backend services using Java/Spring\nDevelop responsive and interactive frontend components with React\nMentor junior developers and participate in code reviews\nCollaborate with product managers to define technical requirements",
                        "140000", "180000", "FULL_TIME", "SENIOR", "Information Technology"
                },
                {
                        "Digital Marketing Specialist",
                        "Join our growing marketing team to drive user acquisition and brand awareness through data-driven digital campaigns.",
                        "3+ years experience in digital marketing\nExpertise in Google Ads and Meta Ads Manager\nStrong analytical skills and experience with GA4\nBachelor's degree in Marketing or related field",
                        "Manage and optimize multi-channel digital advertising campaigns\nAnalyze campaign performance data and provide actionable insights\nConduct A/B testing on ad creatives and landing pages\nCollaborate with the design team to create high-performing ad assets",
                        "70000", "95000", "FULL_TIME", "MID_LEVEL", "Marketing"
                },
                {
                        "Product Designer (UI/UX)",
                        "We're seeking a talented Product Designer to create intuitive and beautiful experiences for our global users.",
                        "Portfolio demonstrating strong UI/UX principles\nProficiency in Figma and Adobe Creative Suite\nExperience with user research and prototyping\nAbility to work in a fast-paced, agile environment",
                        "Create low and high-fidelity wireframes and prototypes\nConduct user testing sessions to validate design decisions\nMaintain and evolve our design system\nWork closely with engineers to ensure design fidelity during implementation",
                        "90000", "130000", "REMOTE", "MID_LEVEL", "Design"
                },
                {
                        "DevOps / Infrastructure Engineer",
                        "Help us build and maintain a robust, automated infrastructure to support our rapidly scaling services.",
                        "5+ years experience in DevOps or SRE roles\nExpertise in Terraform and Kubernetes\nStrong knowledge of AWS or GCP services\nProficiency in scripting languages (Python, Go, or Bash)",
                        "Automate infrastructure provisioning using Terraform\nManage and optimize our Kubernetes clusters\nImplement and maintain CI/CD pipelines\nMonitor system performance and lead incident response efforts",
                        "130000", "170000", "HYBRID", "SENIOR", "Information Technology"
                },
                {
                        "Entry-Level Sales Associate",
                        "Kickstart your career in sales with a dynamic team. We provide extensive training and a clear path for growth.",
                        "Recent graduate or 1 year sales experience\nHighly motivated with excellent communication skills\nResilient attitude and desire to learn\nComfortable with cold calling and outreach",
                        "Identify and qualify new business leads\nConduct product demonstrations for potential clients\nMaintain accurate records in our CRM system\nCollaborate with account managers to close deals",
                        "45000", "65000", "FULL_TIME", "ENTRY_LEVEL", "Sales"
                },
                {
                        "Senior Financial Analyst",
                        "Provide strategic financial guidance and analysis to support our international expansion.",
                        "CPA or CFA qualification required\n6+ years experience in corporate finance\nAdvanced Excel modeling and SQL skills\nExperience with ERP systems (SAP or Oracle)",
                        "Develop comprehensive financial models and forecasts\nPrepare monthly and quarterly performance reports for executives\nAnalyze market trends and competitor financial data\nLead the annual budgeting process across departments",
                        "110000", "145000", "FULL_TIME", "SENIOR", "Finance"
                },
                {
                        "Content Content Strategist",
                        "Shape our brand's voice and lead our content production across all digital platforms.",
                        "4+ years experience in content strategy or copywriting\nStrong editorial skills and attention to detail\nUnderstanding of SEO best practices and social media trends\nExperience managing a content calendar",
                        "Develop and execute a cohesive content strategy\nWrite and edit high-quality blog posts, whitepapers, and social content\nManage a team of freelance writers and designers\nTrack and report on content engagement metrics",
                        "75000", "105000", "REMOTE", "MID_LEVEL", "Marketing"
                },
                {
                        "Customer Success Manager",
                        "Ensure our enterprise clients achieve their goals and see maximum value from our platform.",
                        "3+ years in Customer Success or Account Management\nStrong relationship-building and negotiation skills\nExperience with SaaS products and CRM tools\nProactive problem-solving approach",
                        "Onboard new enterprise clients and drive platform adoption\nConduct regular business reviews to track client success\nIdentify opportunities for upselling and cross-selling\nAct as the voice of the customer for the product team",
                        "80000", "110000", "FULL_TIME", "MID_LEVEL", "Sales"
                }
        };

        for (Object[] jd : jobDataList) {
            Job job = new Job();
            job.setRecruiter(recruiter);
            job.setTitle((String) jd[0]);
            job.setDescription((String) jd[1]);
            job.setRequirements((String) jd[2]);
            job.setResponsibilities((String) jd[3]);
            job.setSalaryMin(new BigDecimal((String) jd[4]));
            job.setSalaryMax(new BigDecimal((String) jd[5]));
            job.setJobType(Job.JobType.valueOf((String) jd[6]));
            job.setExperienceLevel(Job.ExperienceLevel.valueOf((String) jd[7]));

            job.setLocation(job.getJobType() == Job.JobType.REMOTE ? "Remote" : "New York, NY");
            job.setStatus(Job.Status.APPROVED);
            job.setVacancies((int) (Math.random() * 3) + 1);
            job.setDeadline(LocalDate.now().plusWeeks((long) (Math.random() * 4) + 2));

            for (Category cat : categories) {
                if (cat.getName().equals(jd[8])) {
                    job.setCategory(cat);
                    break;
                }
            }

            jobRepository.save(job);
        }
    }
}
