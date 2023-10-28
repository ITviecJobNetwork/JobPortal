INSERT INTO jobs (title, company_id, job_type_id, location_id, min_salary, max_salary, description, requirements, created_by, created_at, expire_at)
VALUES
    ('Software Engineer', 1, 1, 1, 60000, 80000, 'We are looking for a software engineer to join our team.', 'Requirements: Bachelor\'s degree in Computer Science, Java experience.', 'User1', '2023-10-26', '2023-11-30'),
    ('Data Analyst', 2, 2, 2, 50000, 70000, 'We are hiring a data analyst to work on data analysis projects.', 'Requirements: Strong analytical skills, SQL knowledge.', 'User2', '2023-10-26', '2023-12-15'),
    ('UX Designer', 3, 3, 3, 55000, 75000, 'We are seeking a UX designer to improve user experience on our platform.', 'Requirements: Graphic design skills, user-centered design approach.', 'User3', '2023-10-26', '2023-11-30'),
    ('Front-end Developer', 4, 1, 1, 55000, 75000, 'We need a front-end developer to work on our web applications.', 'Requirements: Proficiency in HTML, CSS, and JavaScript.', 'User4', '2023-10-26', '2023-11-30'),
    ('Marketing Specialist', 5, 2, 2, 48000, 68000, 'Join our marketing team as a specialist to promote our products.', 'Requirements: Marketing experience and creativity.', 'User5', '2023-10-26', '2023-12-05'),
    ('Product Manager', 6, 3, 3, 65000, 85000, 'We are looking for a product manager to lead product development.', 'Requirements: Product management experience, strong leadership skills.', 'User6', '2023-10-26', '2023-11-30'),
    ('Data Scientist', 7, 1, 1, 70000, 90000, 'We have an opening for a data scientist to analyze data and make insights.', 'Requirements: Data science expertise, Python skills.', 'User7', '2023-10-26', '2023-12-10'),
    ('Sales Representative', 8, 2, 2, 55000, 75000, 'Join our sales team as a representative to sell our products.', 'Requirements: Sales experience and communication skills.', 'User8', '2023-10-26', '2023-11-30'),
    ('Graphic Designer', 9, 3, 3, 52000, 72000, 'We are hiring a graphic designer to create visual content.', 'Requirements: Graphic design skills and creativity.', 'User9', '2023-10-26', '2023-12-20'),
    ('Network Administrator', 10, 1, 1, 60000, 80000, 'Join our IT team as a network administrator to manage our network infrastructure.', 'Requirements: Network administration experience, CCNA certification.', 'User10', '2023-10-26', '2023-11-30'),
    ('Financial Analyst', 11, 2, 2, 58000, 78000, 'We are looking for a financial analyst to analyze financial data.', 'Requirements: Finance degree, financial analysis skills.', 'User11', '2023-10-26', '2023-12-15'),
    ('Content Writer', 12, 3, 3, 50000, 70000, 'Join our content team as a writer to create engaging content.', 'Requirements: Writing skills and creativity.', 'User12', '2023-10-26', '2023-11-30'),
    ('HR Manager', 13, 1, 1, 65000, 85000, 'We are hiring an HR manager to oversee human resources activities.', 'Requirements: HR management experience, strong interpersonal skills.', 'User13', '2023-10-26', '2023-12-05'),
    ('Software Tester', 14, 2, 2, 53000, 73000, 'Join our QA team as a software tester to test software applications.', 'Requirements: Software testing experience, attention to detail.', 'User14', '2023-10-26', '2023-11-30'),
    ('Data Entry Clerk', 15, 3, 3, 45000, 65000, 'We have an opening for a data entry clerk to input data into our systems.', 'Requirements: Data entry skills and attention to detail.', 'User15', '2023-10-26', '2023-12-10'),
    ('Customer Support Specialist', 16, 1, 1, 48000, 68000, 'Join our customer support team as a specialist to assist customers.', 'Requirements: Customer service skills and communication skills.', 'User16', '2023-10-26', '2023-11-30'),
    ('Business Analyst', 17, 2, 2, 60000, 80000, 'We are looking for a business analyst to analyze business processes.', 'Requirements: Business analysis experience, problem-solving skills.', 'User17', '2023-10-26', '2023-11-30'),
    ('UI Designer', 18, 3, 3, 55000, 75000, 'We are seeking a UI designer to design user interfaces for our applications.', 'Requirements: UI design skills and creativity.', 'User18', '2023-10-26', '2023-11-30'),
    ('Project Manager', 19, 1, 1, 70000, 90000, 'Join our team as a project manager to lead projects to success.', 'Requirements: Project management experience, leadership skills.', 'User19', '2023-10-26', '2023-11-30'),
    ('Marketing Manager', 20, 2, 2, 62000, 82000, 'We are hiring a marketing manager to oversee marketing activities.', 'Requirements: Marketing management experience, strategic thinking.', 'User20', '2023-10-26', '2023-12-15');



/* company */

INSERT INTO company (name, address, phone_number, website, industry, founded_date, description, created_by, created_date, last_modified_by, last_modified_date)
VALUES
    ('Company 1', '123 Main Street, City 1, Country 1', '+123456789', 'www.company1.com', 'Technology', '2000-01-01', 'We are a technology company.', 'User1', '2023-10-26', 'User1', '2023-10-26'),
    ('Company 2', '456 Elm Street, City 2, Country 2', '+987654321', 'www.company2.com', 'Finance', '1995-05-10', 'We provide financial services.', 'User2', '2023-10-26', 'User2', '2023-10-26'),
    ('Company 3', '789 Oak Street, City 3, Country 3', '+555555555', 'www.company3.com', 'Healthcare', '2010-12-15', 'We focus on healthcare solutions.', 'User3', '2023-10-26', 'User3', '2023-10-26'),
    ('Company 4', '101 Pine Street, City 4, Country 4', '+11112222333', 'www.company4.com', 'Education', '2005-03-20', 'We are an educational institution.', 'User4', '2023-10-26', 'User4', '2023-10-26'),
    ('Company 5', '202 Maple Street, City 5, Country 5', '+44443332222', 'www.company5.com', 'Retail', '1990-08-05', 'We operate retail stores.', 'User5', '2023-10-26', 'User5', '2023-10-26'),
    ('Company 6', '303 Cedar Street, City 6, Country 6', '+99998888777', 'www.company6.com', 'Entertainment', '2007-06-25', 'We are in the entertainment industry.', 'User6', '2023-10-26', 'User6', '2023-10-26'),
    ('Company 7', '404 Birch Street, City 7, Country 7', '+77776666555', 'www.company7.com', 'Automotive', '2008-11-30', 'We deal with automotive products.', 'User7', '2023-10-26', 'User7', '2023-10-26'),
    ('Company 8', '505 Redwood Street, City 8, Country 8', '+33332221111', 'www.company8.com', 'Manufacturing', '1998-04-15', 'We are in manufacturing business.', 'User8', '2023-10-26', 'User8', '2023-10-26'),
    ('Company 9', '606 Palm Street, City 9, Country 9', '+77776666555', 'www.company9.com', 'Hospitality', '2002-10-20', 'We provide hospitality services.', 'User9', '2023-10-26', 'User9', '2023-10-26'),
    ('Company 10', '707 Bamboo Street, City 10, Country 10', '+55558888999', 'www.company10.com', 'Agriculture', '1997-07-01', 'We are in the agriculture sector.', 'User10', '2023-10-26', 'User10', '2023-10-26'),
    ('Company 11', '808 Willow Street, City 11, Country 11', '+123456789', 'www.company11.com', 'Technology', '2000-01-01', 'We are a technology company.', 'User1', '2023-10-26', 'User1', '2023-10-26'),
    ('Company 12', '909 Aspen Street, City 12, Country 12', '+987654321', 'www.company12.com', 'Finance', '1995-05-10', 'We provide financial services.', 'User2', '2023-10-26', 'User2', '2023-10-26'),
    ('Company 13', '1011 Pine Street, City 13, Country 13', '+555555555', 'www.company13.com', 'Healthcare', '2010-12-15', 'We focus on healthcare solutions.', 'User3', '2023-10-26', 'User3', '2023-10-26'),
    ('Company 14', '1212 Oak Street, City 14, Country 14', '+11112222333', 'www.company14.com', 'Education', '2005-03-20', 'We are an educational institution.', 'User4', '2023-10-26', 'User4', '2023-10-26'),
    ('Company 15', '1313 Pine Street, City 15, Country 15', '+11112222333', 'www.company15.com', 'Education', '2005-03-20', 'We are an educational institution.', 'User4', '2023-10-26', 'User4', '2023-10-26'),
    ('Company 16', '1414 Pine Street, City 16, Country 16', '+44443332222', 'www.company16.com', 'Retail', '1990-08-05', 'We operate retail stores.', 'User5', '2023-10-26', 'User5', '2023-10-26'),
    ('Company 17', '1515 Pine Street, City 17, Country 17', '+99998888777', 'www.company17.com', 'Entertainment', '2007-06-25', 'We are in the entertainment industry.', 'User6', '2023-10-26', 'User6', '2023-10-26'),
    ('Company 18', '1616 Pine Street, City 18, Country 18', '+77776666555', 'www.company18.com', 'Automotive', '2008-11-30', 'We deal with automotive products.', 'User7', '2023-10-26', 'User7', '2023-10-26'),
    ('Company 19', '1717 Pine Street, City 19, Country 19', '+33332221111', 'www.company19.com', 'Manufacturing', '1998-04-15', 'We are in manufacturing business.', 'User8', '2023-10-26', 'User8', '2023-10-26'),
    ('Company 20', '1818 Pine Street, City 20, Country 20', '+77776666555', 'www.company20.com', 'Hospitality', '2002-10-20', 'We provide hospitality services.', 'User9', '2023-10-26', 'User9', '2023-10-26');


/* candidate_level */
INSERT INTO candidate_level (id, candidate_level)
VALUES
    (1, 'Entry Level'),
    (2, 'Junior Level'),
    (3, 'Mid Level'),
    (4, 'Senior Level'),
    (5, 'Executive Level'),
    (6, 'Associate'),
    (7, 'Assistant'),
    (8, 'Manager'),
    (9, 'Supervisor'),
    (10, 'Coordinator'),
    (11, 'Specialist'),
    (12, 'Director'),
    (13, 'Lead'),
    (14, 'Consultant'),
    (15, 'Architect'),
    (16, 'Analyst'),
    (17, 'Administrator'),
    (18, 'Engineer'),
    (19, 'Technician'),
    (20, 'Designer');


/ * jobType */
-- Active: 1688479680104@@127.0.0.1@3306@job
INSERT INTO job_type (id, job_type)
VALUES
    (1, 'Full-time'),
    (2, 'Part-time'),
    (3, 'Contract'),
    (4, 'Freelance'),
    (5, 'Temporary'),
    (6, 'Internship'),
    (7, 'Remote'),
    (8, 'Permanent'),
    (9, 'Volunteer'),
    (10, 'Seasonal'),
    (11, 'Project-based'),
    (12, 'Shift-based'),
    (13, 'Flex-time'),
    (14, 'Co-op'),
    (15, 'Job Share'),
    (16, 'Hybrid'),
    (17, 'On-call'),
    (18, 'Gig'),
    (19, 'Zero-hour'),
    (20, 'Self-employed');


/ * location */
-- Active: 1688479680104@@127.0.0.1@3306@job
INSERT INTO location (id, city_name)
VALUES
    (1, 'Hanoi'),
    (2, 'Ho Chi Minh City'),
    (3, 'Da Nang'),
    (4, 'Hai Phong'),
    (5, 'Nha Trang'),
    (6, 'Can Tho'),
    (7, 'Vung Tau'),
    (8, 'Quy Nhon'),
    (9, 'Hue'),
    (10, 'Phan Thiet'),
    (11, 'Buon Ma Thuot'),
    (12, 'Rach Gia'),
    (13, 'Bac Lieu'),
    (14, 'Ha Long'),
    (15, 'Long Xuyen'),
    (16, 'Tuy Hoa'),
    (17, 'My Tho'),
    (18, 'Tam Ky'),
    (19, 'Lao Cai'),
    (20, 'Dien Bien Phu');
