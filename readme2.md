##PROGRESS
- Left at 40:50 in Part 1


## Notes
- In Java, an ENUM Class defines a collection of Constants

## Author
- Java Techie
Video name: Spring Boot Security Role Based Authorization Facebook Group management


## PROJECT OVERVIEW
- We are creating a Role-Based API using Spring Boot and Spring security. All the data is
persisted in a MySQL database.

# Depedencies
-Lombok
- Spring Data JPA
-Spring Web
- MySQL Driver

##Packages/Directories
- We will have the folllowing packages:

a.Config
- Contains configuration classes of the application

b.Controller
- Will contain API endpoint URLS and routes

c. Repository
- Contains the JPARepositories. Each repository interface maps against its respective 
entity (in the entity package).
- The repository interfaces interacts directly with the database.
- Henceforth, they are called/invoked directly from the controller or service to create,
save, update or delete data.
- The interfaces (meaning they contain abstract methods) in this package extends 
JPARepository
- On each repository we pass generic classes of Entity and the primary key in that entity
**public interface UserRepository extends JpaRepository<User, Id> { ... }**

d. Entity
- They hold POJO classes that define the structure of each database table we create.
- When you run the application, JDBC reads the entity classes and create the tables 
as specified in the application.properties file.
-Entity classes also contain getters and setters that are used for getting and setting data
to the table. 
- You can use lombok generator to autogenerate the getters and setters or generate them 
yourself 

## Config Package
- This package contains a collection of configuration classes.

1. SecurityConfig Class
- We will customize Spring security authentication and authorization 
- We will add the following global level security annotations
- @Configuration - Spring annotation that specifies this is a configuration class.
- @EnableWebSecurity- A spring security marker annotation. it allows Spring to find and 
automatically apply the class to the global security. 
- @EnableGlobalMethodSecurity- This annotation enables Spring security global method 
security similar to the <global-method-security> xml support. This annotation has optional
elements/parameters which include:

securedEnabled : Determines if Spring security secured annotations should be enabled, its
false by default.

prePostEnabled : Determines if Spring Security's pre-post annotations should be enabled,
its false by default.

- This class will extend the WebSecurityConfigurerAdapter spring security class. This means 
we will have access to all the Spring Security DSL methods. 
-Inside the method the 1st method to override is the configure class. The one that has 
AuthenticationManagerBuilder as the parameter. This method is used for Authentication purpose
only.
-Inside the method we get the auth object then the userDetailsService method. This method
will take in the userDetailsService object(This object comes from Spring Security).
-The class will be called userDetailsService and we will inject it into the SecurityConfig 
class as a depededency injection. the class comes from Spring Security.
- Hence we will use @Autowired annotation to tell spring that this is a depedency injection. 

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecuityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}

## Creating a Custom Class 
- We will create a custom calss called GroupUserDetailsService  that implements 
UserDetailsService
- The purpose of this class will be to load userdetails from the database after passing in 
teh username. 
- In the class we will override the loadUserByUserName method.
-Inside the method we will reach out to user repositoty (inject it using Autowired depedency)
then store in a variable called user (which is optional meaning it may or may not find the
user). We pass a generic type of User object.
-Since the return type of loadByUserName is UserDetails object. We need to create another
class called GroupUserDetails (that implements Spring security UserDetails object) and map
teh loaded user from the database to that object

## Creating GroupUserDetails class.
- create the class and it will implement UserDetails interface. 
- Inside the class you will override all the custom methods. Inside the class add a 
constructor method called GroupUserDetails that will be called first immediately the 
class is instantiated. 
- Inside the constrauctor method, we assign each property to what is passed when calling/invoking
this class

## Invoking GroupUsrDetails class 
- To fetch it, we reach out to user then map method. Inside the method we instantiate the  
class we are mapping our data to. If there is no user throw a custom exception/error message.

2. Controller
-This package contains our API endpoints.

a. UserController
- This class will contain the user API endpoints.
- We will add the following annotations: 
@RestController - @RestController annotation was introduced in Spring 4.0 to 
simplify the creation of RESTful web services. It's a convenience annotation that 
combines @Controller and @ResponseBody

@RequestMapping - Will contain a parameter of the URL to pass. Adding it on top of the 
class means all API endpoints must have /user url.

- This Controller will contain teh following methods:
1. joinGroup - This is an API endpoint to add a new user and save to the database.
It will conatin parameter of User object that we will persist to our database. 
- We save the user object to teh datbase via the repository.
- We will also encrypt the password before saving.
- By default we will assign the role as ROLE_USER. So the code will look like:

@RestController
@RequestMapping("/user")
public class UserController {

    private static final String DEFAULT_USER = "ROLE_USER";

    @Autowired
    private UserRepository repository;
    
    //This depedency is pulled from the SecurityConfig class in PasswordEncoder bean
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/join")
    public String joinGroup(@RequestBody User user){
        user.setRoles(DEFAULT_USER);
        String encryptedPwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPwd);
        repository.save(user);
        return "Hi" + user.getUserName() + " ,welcome to group";
    }
}

2. giveAccessToUser
- This endpoint will be used to give acess to a logged in user. 
- It will be a get request henc we use GetMapping annotation. On the URL we pass a dynamic
userId and userRole. 
- We pass this parameters into the method by using @pathVariable annotation. 
- We also pass the Principal object that represents the logged in user. 

## Method Level Security 
Spring Security provides method level security using @PreAuthorize and @PostAuthorize 
annotations. This is expression-based access control. 

1. @PreAuthorize
The @PreAuthorize annotation checks the given expression before entering the method, 
whereas, the @PostAuthorize annotation verifies it after the execution of the method 
and could alter the result.

   @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> loadUsers(){
        return repository.findAll();
    }


2. @Secured
The @Secured annotation is used to specify a list of roles on a method. 
Hence, a user only can access that method if she has at least one of the specified roles.

   @GetMapping
    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> loadUsers(){
        return repository.findAll();
    }
    
## Authorization
- After adding all the API endpoints we can now add the authorization filters inside
SecurityConfig class in configure method (one with HttpSecurity parameter).
- In teh method we will allow all requests coming from /user/join  (we disable all
security by adding permitALL). 

http.authorizeRequests()
.antMatchers("/user/join")
.permitAll()
                
- If teh URL has /user/** or post/** URL we authenticate the requests and use
httpBasic authentication method.
 
 .authorizeRequests()
 .antMatchers("/user/**", "/post/**")
 .authenticated().and().httpBasic();


 @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/join")
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/user/**", "/post/**")
                .authenticated().and().httpBasic();
    }








