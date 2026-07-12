# Public Pages

## Page: Home / Landing Page
- Route/Path: `/`
- Role: Public
- Purpose: Marketing landing page showcasing the platform's value proposition with job search, categories, and recent listings.
- Key UI Elements: Hero section with headline and search form (keyword input, location input, "Find Jobs" button); stats bar (12K+ Live Jobs, 8K+ Companies, 200K+ Candidates, 95% Success Rate); dynamic category cards grid with icons; "How It Works" 3-step section; recent job cards grid (company logo, type badge, title, company, location, salary, "Apply Now" button); final CTA section; popular search tags (Software, Design, Marketing); mobile "Start Your Search" button
- Core Actions: Search jobs by keyword/location; browse categories; view recent job listings; navigate to registration
- Data Displayed: 8 most recent approved jobs (title, company, type, location, salary); active job categories with icons
- Navigation: Search form → `/jobs/search`; category cards → `/jobs/search?categoryId={id}`; job titles → `/jobs/{id}`; "Apply Now" → `/jobs/{id}`; "View All Opportunities" → `/jobs`; "Get Started Now" → `/register`; "Start Your Search" (mobile) → `/jobs`

## Page: Login
- Route/Path: `/login`
- Role: Public
- Purpose: Authenticate existing users with email and password credentials.
- Key UI Elements: Centered card with "Welcome Back" heading; email input (type email, placeholder "name@example.com", envelope icon); password input (placeholder "••••••••", lock icon); "Keep me logged in" checkbox (remember-me); "Sign In" button (full-width, primary, rounded-pill); conditional alert messages (error/logout/success); "Forgot?" link (non-functional, points to #); "Create Account" link
- Core Actions: Submit login credentials; toggle remember-me; navigate to registration
- Data Displayed: Error messages (invalid credentials, account locked); logout confirmation; success messages (after registration)
- Navigation: "Create Account" → `/register`; successful login redirects to role-based dashboard; "Forgot?" → `#` (non-functional)

## Page: Register
- Route/Path: `/register`
- Role: Public
- Purpose: Create a new account as either a Job Seeker or Recruiter.
- Key UI Elements: Split card layout (branding left column hidden on mobile, form right column); role selection as styled radio-button cards ("Seeker" with person-badge icon / "Recruiter" with building icon); email input (type email, required); password input (required, half-width); confirm password input (required, half-width); "Create My Account" button (full-width, primary, rounded-pill); validation error spans per field; left branding with feature bullets ("Access to 10k+ premium jobs", "Post jobs and manage candidates")
- Core Actions: Select role (JOB_SEEKER or RECRUITER); fill registration form; submit account creation
- Data Displayed: Validation errors inline per field; branding/feature text
- Navigation: "Login Here" → `/login`; successful registration redirects to `/login` with success message

## Page: Browse Jobs
- Route/Path: `/jobs`
- Role: Public
- Purpose: Paginated grid listing of all approved job postings.
- Key UI Elements: Header with "Explore All Opportunities" heading and total count badge; job cards grid (3 columns lg, 2 columns md) showing company logo/fallback icon, job type badge, title link, company name with verified badge, location, posted date, salary range, "View Details" button; pagination (Previous/Next chevrons with page number); empty state (briefcase icon, "No jobs available", "Go to Home" button)
- Core Actions: Browse paginated job listings; navigate to job detail; paginate through results
- Data Displayed: Total job count; per job: title, company name, verified status, location, posted date (dd MMM yyyy), salary range, job type
- Navigation: Job titles → `/jobs/{id}`; "View Details" → `/jobs/{id}`; pagination → `/jobs?page={n}`; "Go to Home" → `/`

## Page: Job Search
- Route/Path: `/jobs/search`
- Role: Public
- Purpose: Advanced job search with multi-criteria filtering and paginated results.
- Key UI Elements: Search header with info card; mobile filter toggle button; sidebar filter form (keyword input, location input, category dropdown, job type radio pills, experience level radio pills, minimum salary number input, "Apply Filters" button, "Clear All" link); results header with count badge and sort dropdown (Newest/Oldest/Salary — non-functional); job cards grid (2 columns lg) with company logo, type badge, title, company, location, salary, "View Details" button; save/bookmark toggle button (JOB_SEEKER only); pagination with all filter params preserved; empty state with "Clear all filters" button
- Core Actions: Filter jobs by keyword, location, category, job type, experience level, minimum salary; paginate results; save/bookmark jobs (job seekers only); clear filters
- Data Displayed: Total results count; per job: title, company, verified badge, location, type badge, salary; saved state per job (for job seekers)
- Navigation: "View Details" → `/jobs/{id}`; "Clear All" → `/jobs/search`; pagination preserves search params; bookmark toggle → AJAX POST `/job-seeker/saved-jobs/toggle/{id}`

## Page: Job Detail
- Route/Path: `/jobs/{id}`
- Role: Public (with role-conditional elements)
- Purpose: Display complete job posting details and provide application functionality for job seekers.
- Key UI Elements: Header with company logo, breadcrumb (Explore Jobs → Category), job title, company name with verified badge, location, posted date; bookmark button (JOB_SEEKER only); "Application Sent" badge (if applied); "Position Closed" badge (if closed); Job Overview card (salary, location, type, experience level); rich HTML sections for description, responsibilities, requirements, about company; sidebar application card with cover letter textarea (4 rows) and "Submit Application" button; "Send a Message" button (after applying); "Login to Apply" / "Register now" (guest view); company summary card (logo, name, verified status, location, website link); social share buttons (LinkedIn, Twitter/X, Copy Link — non-functional); "All jobs from this company" button
- Core Actions: Read full job details; apply with optional cover letter (job seekers); save/bookmark job (job seekers); message recruiter (after applying); share job (non-functional)
- Data Displayed: Job title, company name/logo, verified status, location, posted date, salary range, job type, experience level, full description (HTML), responsibilities (HTML), requirements (HTML), company description (HTML), company website
- Navigation: Breadcrumb "Explore Jobs" → `/jobs/search`; "Submit Application" → POST `/job-seeker/apply/{id}`; "Send a Message" → `/messages/chat/{recruiterId}`; "Login to Apply" → `/login`; "Register now" → `/register`; "All jobs from this company" → `/jobs/search?recruiterId={id}`

---

# Job Seeker Pages

## Page: Job Seeker Dashboard
- Route/Path: `/job-seeker/dashboard`
- Role: Job Seeker
- Purpose: Central hub showing activity summary, profile strength, recent applications, recommended jobs, and saved jobs.
- Key UI Elements: Welcome message with profile name; 4 stat cards (Total Applications, Interviews, Saved Jobs, Shortlisted); profile strength card with avatar, fullName, verified badge, completion percentage progress bar; recent applications table (Opportunity, Status badge, Date, Details button); recommended jobs cards (type badge, title, company, salary); saved jobs list with remove button (X icon); empty states ("No applications yet" with "Start Searching" button, "No jobs saved yet")
- Core Actions: View activity overview; navigate to profile editing; navigate to applications; navigate to job search; remove saved jobs via AJAX toggle; view application details
- Data Displayed: Application count, interview count, saved jobs count, shortlisted count; profile picture, full name, profile strength percentage; recent applications (job title, company, status, date); recommended jobs (title, company, salary, type); saved jobs (title, company, location)
- Navigation: "Find New Jobs" → `/jobs/search`; "Edit Profile" → `/job-seeker/profile`; "My Applications" → `/job-seeker/applications`; "Saved Jobs" → `/job-seeker/saved-jobs`; "Update Resume" → `/job-seeker/profile`; "View History" → `/job-seeker/applications`; "Details" per app → `/jobs/{id}`; "View All" saved → `/job-seeker/saved-jobs`

## Page: Job Seeker Profile
- Route/Path: `/job-seeker/profile`
- Role: Job Seeker
- Purpose: Edit professional profile including personal info, skills, education, and generate a PDF resume.
- Key UI Elements: Left column with "Personal Highlights" card (fullName text input, about field with Quill rich text editor, phone input with icon, location input with icon) and "Education & Technical Skills" card (skills textarea, education textarea); right column with profile avatar card (completion progress bar hardcoded 85%), "Profile Perfection" tip card, "ATS-Friendly Resume" PDF generator card with "Download PDF" link; "Save Final Profile" submit button; success/error alerts
- Core Actions: Edit full name, about (rich text), phone, location, skills, education; save profile; generate and download PDF resume
- Data Displayed: Current profile data pre-filled in form; profile completion percentage (hardcoded 85%); success/error messages
- Navigation: "Save Final Profile" → POST `/job-seeker/profile`; "Download PDF" → `/job-seeker/resume/generate` (new tab); sidebar links to other dashboard pages

## Page: Job Seeker Applications
- Route/Path: `/job-seeker/applications`
- Role: Job Seeker
- Purpose: View all submitted job applications with detailed status timeline and management actions.
- Key UI Elements: "Apply for More" button; per-application cards with left section (job title link, company, location, applied date, status badge) and right section (4-step status timeline: Submitted → Shortlisted → Interview with date/location → Final Decision); "Full Details" button opening modal; "Withdraw Application" button with confirm dialog; detail modals showing cover letter and recruiter notes; empty state with "Browse Jobs" button
- Core Actions: View application status timeline; view cover letter and recruiter notes in modal; withdraw application; navigate to job details
- Data Displayed: Per application: job title, company, location, applied date, current status (APPLIED/VIEWED/SHORTLISTED/INTERVIEW_SCHEDULED/INTERVIEWED/OFFERED/HIRED/REJECTED/WITHDRAWN), interview date/location, cover letter, recruiter status note
- Navigation: "Apply for More" → `/jobs/search`; job title → `/jobs/{id}`; "Withdraw" → POST `/job-seeker/applications/{id}/withdraw`; "Browse Jobs" (empty) → `/jobs/search`

## Page: Saved Jobs
- Route/Path: `/job-seeker/saved-jobs`
- Role: Job Seeker
- Purpose: Grid view of all bookmarked jobs with quick access to apply or remove.
- Key UI Elements: "Find More Jobs" button; responsive grid (1/2/3 columns) of job cards with building icon, bookmark-fill toggle button, job title link, company name, type badge, location, salary, "Apply Now" link; empty state (bookmark icon, "No Saved Jobs", "Browse Jobs" button); confirm dialog on remove
- Core Actions: Remove bookmarked jobs (AJAX toggle with DOM removal); navigate to job detail to apply; browse more jobs
- Data Displayed: Per saved job: title, company name, job type badge, location, salary (min)
- Navigation: "Find More Jobs" → `/jobs/search`; job title → `/jobs/{id}`; "Apply Now" → `/jobs/{id}`; "Browse Jobs" (empty) → `/jobs/search`; bookmark toggle → POST `/job-seeker/saved-jobs/toggle/{id}`

---

# Recruiter Pages

## Page: Recruiter Dashboard
- Route/Path: `/recruiter/dashboard`
- Role: Recruiter
- Purpose: Overview of recruiter activity with stats, recent applicants, company profile card, and quick action links.
- Key UI Elements: Header with "Post New Job" button; 4 stat cards (Total Jobs with "Active" badge, Applicants "All time", Interviews "Scheduled", Live Positions); recent applicants table (Candidate name + email, Position Applied, Status badge color-coded, Applied Date); company profile card (logo/fallback, verified badge, company name, location, status, website, "Edit Profile Settings" button — or "Complete your Profile" prompt if none exists); quick links card ("Post a New Job", "Manage Job Listings", "Review Applications")
- Core Actions: Navigate to post new job; view recent applicants; edit company profile; access job management and applications
- Data Displayed: Job count, application count, interview count, open positions; recent applications (candidate name, email, job title, status, date); company profile (logo, name, location, website, verified status)
- Navigation: "Post New Job" → `/recruiter/jobs/create`; "View All" → `/recruiter/applications`; "Edit Profile Settings" / "Get Started" → `/recruiter/profile`; "Post a New Job" → `/recruiter/jobs/create`; "Manage Job Listings" → `/recruiter/jobs`; "Review Applications" → `/recruiter/applications`

## Page: Recruiter Company Profile
- Route/Path: `/recruiter/profile`
- Role: Recruiter
- Purpose: Create or update company profile information including branding and description.
- Key UI Elements: Left column "Identity & Branding" card with company name input (required), Quill rich text editor for company description, website URL input with globe icon, headquarters location input with geo icon; "Save Company Profile" submit button (primary, rounded-pill); right column "Profile Tips" card (dark bg with 3 tips about improving profile); success/error alerts
- Core Actions: Edit company name, description (rich text), website URL, location; save profile
- Data Displayed: Current profile data pre-filled; tip messages about profile optimization
- Navigation: "Save Company Profile" → POST `/recruiter/profile`; sidebar navigation to other recruiter pages

## Page: Manage Job Listings
- Route/Path: `/recruiter/jobs`
- Role: Recruiter
- Purpose: Table view of all recruiter's job postings with stats, status tracking, and CRUD actions.
- Key UI Elements: "Post New Job" button; 3 quick stat cards (Active Jobs count, Pending/Draft count, Total Candidates count); jobs table with columns (Role & Details with title + category badge + date, Metadata with location + salary, Status badge color-coded, Candidates clickable count, Actions); per-row actions dropdown ("Live View" new tab, "Edit Posting", "Delete Job" with confirm); success alert; empty state with "Post Your First Job" button
- Core Actions: View all job listings with status; navigate to create new job; edit existing job; delete job (with confirmation); view job applicants; open live public view
- Data Displayed: Per job: title, category, creation date, location, salary range, status (APPROVED/PENDING/CLOSED), application count
- Navigation: "Post New Job" → `/recruiter/jobs/create`; candidates count → `/recruiter/jobs/{id}/applications`; "Applicants" → `/recruiter/jobs/{id}/applications`; "Live View" → `/jobs/{id}` (new tab); "Edit Posting" → `/recruiter/jobs/{id}/edit`; "Delete Job" → POST `/recruiter/jobs/{id}/delete`; "Post Your First Job" (empty) → `/recruiter/jobs/create`

## Page: Create/Edit Job Posting
- Route/Path: `/recruiter/jobs/create` (new) or `/recruiter/jobs/{id}/edit` (edit)
- Role: Recruiter
- Purpose: Form for creating a new job posting or editing an existing one with rich text fields.
- Key UI Elements: Dynamic header ("Create New Opportunity" or "Refine Job Posting"); "Back to My Jobs" button; left column "Role Information" card (job title text input required, 3 Quill rich text editors for description/requirements/responsibilities with hidden sync inputs); right column "Logistics & Compensation" card (location input required, category select dropdown required, employment type select required — FULL_TIME/PART_TIME/CONTRACT/INTERNSHIP/REMOTE, experience level select required — ENTRY_LEVEL/MID_LEVEL/SENIOR/EXECUTIVE, min salary number, max salary number, deadline date input required, vacancies number input min=1); submit button ("Publish Opportunity" or "Apply Changes", full-width, rounded-pill)
- Core Actions: Fill all job fields; write rich text descriptions; select category/type/level; set salary range and deadline; publish or update job
- Data Displayed: Pre-filled form data on edit; category options from database; enum values for type and level
- Navigation: "Back to My Jobs" → `/recruiter/jobs`; submit → POST `/recruiter/jobs/create` or POST `/recruiter/jobs/{id}/edit`; success redirects to `/recruiter/jobs`

## Page: Applicant Tracking (Kanban Board)
- Route/Path: `/recruiter/applications`
- Role: Recruiter
- Purpose: Drag-and-drop Kanban board for tracking candidates through the hiring pipeline with detailed candidate review.
- Key UI Elements: Header with Filter/Sort/Export button group; success/error alerts; 5 Kanban columns (Applied — blue, In Review — cyan, Interviewing — yellow, Hired — green, Rejected — red); per-application cards (avatar, candidate name clickable, job title, dropdown with "Review"/"Schedule", experience badge, date); View Modal per application (candidate name, email, phone, skills badges, education, cover letter, "Download Resume" button, "Chat with Candidate" button, "Schedule Interview" button, interview info box, status note form with textarea and "Save Note" button); Schedule Interview Modal (date input, time input, location text input, notes textarea, "Cancel"/"Send Invite" buttons); SortableJS drag-and-drop between columns with AJAX status updates
- Core Actions: Drag cards between pipeline stages (AJAX status update); review full candidate details in modal; download resume; chat with candidate; schedule interview (date/time/location/notes); save status notes; filter/sort (UI buttons present)
- Data Displayed: Per application: candidate name, job title, experience level, date (applied/interview/status update), status; in modal: email, phone, skills, education, cover letter, resume link, interview datetime/location, status note; column counts
- Navigation: Candidate name → opens View Modal; "Download Resume" → `/uploads/resumes/{file}`; "Chat with Candidate" → `/messages/chat/{userId}`; drag-drop → AJAX POST `/recruiter/applications/{id}/status-ajax`; "Send Invite" → POST `/recruiter/applications/{id}/schedule-interview`; "Save Note" → POST `/recruiter/applications/{id}/status`

## Page: Job Applicants (per job)
- Route/Path: `/recruiter/jobs/{id}/applications`
- Role: Recruiter
- Purpose: View applications filtered to a specific job posting (uses same Kanban template as general applications).
- Key UI Elements: Same Kanban board layout as `/recruiter/applications` but filtered to one job; job context available; all modals and drag-drop functionality identical
- Core Actions: Same as general applicant tracking but scoped to one job's applicants
- Data Displayed: Applications for the specific job only; job title context
- Navigation: Same as recruiter applications page; status update redirects back to `/recruiter/jobs/{jobId}/applications`

---

# Admin Pages

## Page: Admin Dashboard
- Route/Path: `/admin/dashboard`
- Role: Admin
- Purpose: Platform-wide analytics dashboard showing system statistics, growth metrics, and registration trends.
- Key UI Elements: "Export Report" button (decorative); 4 stat cards (Total Users with growth badge, Live Jobs with growth badge, Applications with growth badge, Pending Review with "Health" badge); User Registration Trends line chart (Chart.js, 7 days, blue line with fill); User Distribution doughnut chart (Chart.js, Job Seekers vs Recruiters, blue/green); Recent Platform Activity table (User Details with avatar + email + UID, Role badge, Status badge Active/Disabled, Joined Date, Actions dropdown with "Details"/"Suspend" links); "View All Users" link
- Core Actions: Monitor platform statistics; view registration trends over 7 days; view user distribution; access user details; navigate to user management
- Data Displayed: Total users (job seekers + recruiters), approved jobs count, total applications, pending jobs; growth numbers (new users/jobs/applications this month); daily registration data (7 days); user distribution pie; recent job seekers (email, ID, role, enabled status, join date)
- Navigation: "View All Users" → `/admin/users`; "Details" per user → link; "Suspend" per user → link; sidebar navigation

## Page: User Management
- Route/Path: `/admin/users`
- Role: Admin
- Purpose: Manage all platform users with filtering, pagination, and account control actions.
- Key UI Elements: Header with "Excel"/"PDF" export buttons (decorative); filter card with role select (All/JOB_SEEKER/RECRUITER/ADMIN), account status select (Any/Active/Disabled), lock state select (Any/Unlocked/Locked), "Apply" button, "Reset" link; users table (User Identity with avatar + email + join date, Platform Role badge color-coded, Account Status with colored dot + text, Security with lock/shield status, Quick Actions button group); toggle status button (enable/disable icon), toggle lock button (lock/unlock icon); pagination (Previous/page numbers/Next preserving filters)
- Core Actions: Filter users by role, enabled status, lock state; enable/disable user accounts; lock/unlock user accounts; paginate through users; reset filters
- Data Displayed: Per user: email, join date (MMM yyyy), role (badge), enabled/disabled status, locked/unlocked status; paginated list with total pages
- Navigation: "Apply" filter → GET `/admin/users` with params; "Reset" → `/admin/users`; toggle status → POST `/admin/users/{id}/toggle-status`; toggle lock → POST `/admin/users/{id}/toggle-lock`; pagination → `/admin/users?page={n}&role=&enabled=&locked=`

## Page: Manage Jobs (Admin)
- Route/Path: `/admin/jobs`
- Role: Admin
- Purpose: Moderate job postings by approving, rejecting, or deleting them with status-based filtering.
- Key UI Elements: 4 stat cards (Total — blue, Approved — green, Pending — yellow, Rejected — red); tab navigation (Pending/Approved/Rejected); success/error alerts; jobs table (Title link, Company, Location, Status badge, Posted date, Actions); conditional action buttons per status (Approve/Reject for PENDING; Reject for APPROVED; Approve for REJECTED); delete button (trash icon) opening confirmation modal per job; delete modal with warning text and Cancel/Delete buttons; empty state per tab
- Core Actions: Filter jobs by status tab; approve pending jobs; reject jobs; delete jobs (with confirmation modal); view job details via title link
- Data Displayed: Stats (total, approved, pending, rejected counts); per job: title, company name, location, status, posted date (MMM dd, yyyy)
- Navigation: Tabs → `/admin/jobs?status=PENDING|APPROVED|REJECTED`; title link → `/jobs/{id}`; "Approve" → POST `/admin/jobs/{id}/approve`; "Reject" → POST `/admin/jobs/{id}/reject`; "Delete" → POST `/admin/jobs/{id}/delete`

## Page: Manage Categories
- Route/Path: `/admin/categories`
- Role: Admin
- Purpose: CRUD interface for managing job categories used across the platform.
- Key UI Elements: "Manage Categories" heading; add category form card (name text input required with placeholder "Category Name", description text input optional, "Add" button); categories table (Name, Description, Active badge — green "Active" or grey "Inactive", Actions with Delete button — red, small)
- Core Actions: Add new category (name + optional description); delete existing category
- Data Displayed: All categories with name, description, active/inactive status
- Navigation: Add form → POST `/admin/categories`; Delete → POST `/admin/categories/{id}/delete`

---

# Shared Authenticated Pages

## Page: Message Inbox
- Route/Path: `/messages`
- Role: Job Seeker / Recruiter / Admin (any authenticated user)
- Purpose: List all conversations with other users showing latest message previews.
- Key UI Elements: "Your Messages" heading; conversation list items (person avatar circle, other user's full name, last message preview prefixed with "You: " if sent by current user, timestamp, "New" badge for unread); empty state (chat-dots icon, "No messages yet")
- Core Actions: View conversation list; navigate to individual chat; identify unread conversations
- Data Displayed: Per conversation: other user's name, last message preview (truncated), timestamp (dd MMM yyyy, HH:mm), unread indicator
- Navigation: Each conversation → `/messages/chat/{otherUserId}`

## Page: Chat Conversation
- Route/Path: `/messages/chat/{otherUserId}`
- Role: Job Seeker / Recruiter / Admin (any authenticated user)
- Purpose: One-on-one messaging interface with another user.
- Key UI Elements: Chat header (back arrow, avatar, other user's name, "Online" green dot indicator); scrollable message area with bubbles (sent = blue/right-aligned, received = white/left-aligned, each with HH:mm timestamp); message input form (hidden receiverId field, text input with placeholder "Type a message...", circular blue send button with icon)
- Core Actions: Send text messages; read conversation history; navigate back to inbox
- Data Displayed: Full conversation history (message content, timestamps, sent/received distinction); other user's name and online status
- Navigation: Back button → `/messages`; send form → POST `/messages/send` (redirects back to chat)

## Page: Notification Center
- Route/Path: `/notifications`
- Role: Job Seeker / Recruiter / Admin (any authenticated user)
- Purpose: Paginated list of all notifications with read/unread status and navigation to related content.
- Key UI Elements: "Notification Center" heading; "Mark All as Read" button; notification list items (type-colored icon — interview/info, rejected/danger, hired/success, default/primary; title; message; timestamp; "New" badge for unread; blue left border + light bg for unread items); pagination (Previous/Next + page numbers); empty state (bell-slash icon, "No notifications yet")
- Core Actions: View all notifications; mark all as read; navigate to notification target (dynamic link); paginate
- Data Displayed: Per notification: icon (type-based), title, message, timestamp (dd MMM, HH:mm), read/unread state; unread count
- Navigation: "Mark All as Read" → `/notifications/mark-all-read`; each notification → `notif.link` (dynamic); pagination → `/notifications?page={n}`
