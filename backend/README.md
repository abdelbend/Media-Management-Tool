<h1 style="
    font-size: 2.5em; 
    color: #4A90E2; 
    background-color: #F4F4F8; 
    padding: 15px 20px; 
    text-align: center; 
    border-radius: 8px; 
    font-family: Arial, sans-serif; 
    box-shadow: 3px 3px 8px rgba(0, 0, 0, 0.2);
">
    Backend Structure
</h1>


<p>
  Welcome to the <strong>Library Management Backend System</strong>, a Spring Boot project structured to ensure clarity, organization, and team collaboration without conflicts. This backend structure is designed to support a clean, maintainable, and efficient development environment, making it easy to scale and collaborate.
</p>

<h2>Project Structure</h2>
<pre style="
    background-color: #2E3440; 
    color: #D8DEE9; 
    padding: 15px; 
    font-size: 1em; 
    border-radius: 8px; 
    font-family: 'Courier New', monospace; 
    overflow-x: auto;
    box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.2);
    line-height: 1.5;
    ">
src/
├── main/
│   ├── java/com/example/backend/
│   │   ├── config/                # Configuration files (e.g., WebConfig, SecurityConfig)
│   │   ├── controller/            # REST controllers for handling HTTP requests
│   │   ├── dto/                   # Data Transfer Objects (DTOs) for request and response encapsulation
│   │   ├── model/                 # JPA entity classes representing database tables
│   │   ├── exception/             # Custom exceptions and global error handling
│   │   ├── repository/            # Spring Data JPA repository interfaces
│   │   ├── service/               # Business logic and service layer
│   │   ├── util/                  # Utility classes for shared helper methods
│   │   └── BackendApplication.java # Main Spring Boot application class
│   │
│   └── resources/
│       └── application.properties # Configuration properties (e.g., database settings, server port)
│
└── test/
    └── java/com/example/backend/  # Unit and integration tests

</pre>


<h2>Folder Explanation</h2>
<h3>resources/</h3>
<ul>
  <li><strong>application.properties</strong>: Configurations for database, server port, and other properties.</li>
</ul>

<h3>test/</h3>
<ul>
  <li>Contains unit and integration tests for all components to ensure functionality and reliability.</li>
</ul>

<h3>1. <strong>config/</strong></h3>
<ul>
  <li>Holds configuration classes such as <code>WebConfig</code>, <code>SecurityConfig</code>, and database configurations.</li>
  <li>Manages application settings to ensure seamless integration and operation of components.</li>
</ul>

<h3>2. <strong>controller/</strong></h3>
<ul>
  <li>Contains REST controllers for handling HTTP requests.</li>
  <li>Each entity or feature has a dedicated controller, for example, <code>MediaController</code>, <code>UserController</code>, etc.</li>
</ul>

<h3>3. <strong>dto/</strong> (Data Transfer Object)</h3>
<ul>
  <li>Contains DTO classes for request and response data encapsulation.</li>
  <li>By using DTOs, entities are kept separate from client data, enhancing security and flexibility.</li>
</ul>

<h3>4. <strong>model/</strong></h3>
<ul>
  <li>Houses JPA entity classes that represent database tables, such as <code>Media</code>, <code>Member</code>, <code>User</code>, etc.</li>
</ul>

<h3>5. <strong>exception/</strong></h3>
<ul>
  <li>Contains custom exception classes.</li>
  <li>Includes a global exception handler (e.g., <code>GlobalExceptionHandler, NotFoundException ...</code>) for consistent error handling across the application.</li>
</ul>

<h3>6. <strong>repository/</strong></h3>
<ul>
  <li>Contains Spring Data JPA repository interfaces to interact with the database.</li>
  <li>For example, <code>MediaRepository</code>, <code>UserRepository</code>, etc.</li>
</ul>

<h3>7. <strong>service/</strong></h3>
<ul>
  <li>Houses service classes implementing business logic.</li>
  <li>For example, <code>MediaService</code> manages operations related to books, such as adding, deleting, and fetching.</li>
</ul>

<h3>8. <strong>util/</strong></h3>
<ul>
  <li>Contains utility classes with shared helper methods or constants across the application.</li>
</ul>

<h2>Main Application File</h2>
<ul>
  <li><strong>LibraryManagementApplication.java</strong>:
    <ul>
      <li>The main class annotated with <code>@SpringBootApplication</code> to launch the Spring Boot application.</li>
    </ul>
  </li>
</ul>

<h2>Configuration File</h2>
<ul>
  <li><strong>application.properties</strong>:
    <ul>
      <li>Set configurations for database (e.g., MariaDB, or MySQL), server port, and other essential properties.</li>
    </ul>
  </li>
</ul>

<h2>Getting Started</h2>
<p>To get started with the project, follow these steps:</p>
<ol>
  <li><strong>Clone the Repository</strong>
    <pre><code>git clone https://git.uni-wuppertal.de/adampos/tool-zur-medienverwaltung.git</code></pre>
  </li>

  <li><strong>Import the Project</strong>
    <p>Open your preferred IDE and import the project as a Maven or Gradle project.</p>
  </li>

  <li><strong>Configure Database</strong>
    <p>Update the <code>application.properties</code> file in the <code>resources/</code> directory with your database configurations.</p>
  </li>

  <li><strong>Run the Application</strong>
    <p>Run <code>BackendApplication.java</code> with Spring Boot to start the backend server.</p>
  </li>
</ol>

<h5 style="color: #ff4d4d; background-color: #333333; padding: 10px; border-radius: 5px; font-weight: bold; text-align: center; box-shadow: 2px 2px 5px rgba(0,0,0,0.3);">To work on the backend, open only the backend directory in IntelliJ IDEA. This ensures IntelliJ recognizes it as a Spring Boot application, enabling full framework support and optimized development features.</h5>