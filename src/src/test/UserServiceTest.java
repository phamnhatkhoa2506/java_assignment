package test;

import service.UserService;
import entities.User;

import java.util.List;

public class UserServiceTest {
    public static void main(String[] args) {
    	try {
            UserService userService = new UserService();

//            // 1. Register a new user
//            System.out.println("== Register new user ==");
//            User newUser = new User(
//                "svcuser",
//                "svcpass",
//                "svcuser@example.com",
//                "Svc",
//                "User",
//                LocalDate.of(1995, 5, 15)
//            );
//            User registered = userService.register(newUser);
//            System.out.println("Registered: " + registered);

//            // 2. Attempt to register with existing username/email
//            System.out.println("\n== Attempt duplicate register ==");
//            User duplicate = new User(
//                UUID.randomUUID(),
//                "svcuser",  // same username
//                "anotherpass",
//                "svcuser@example.com",  // same email
//                "Test",
//                "Dup",
//                LocalDate.of(2000, 1, 1)
//            );
//            User dupResult = userService.register(duplicate);
//            System.out.println("Duplicate register result: " + dupResult);

            // 3. Login with correct credentials
            System.out.println("\n== Login success ==");
            User loginOk = userService.login("svcuser", "svcpass");
            System.out.println("Login ok: " + loginOk);

//            // 4. Login with incorrect password
//            System.out.println("\n== Login fail ==");
//            User loginFail = userService.login("svcuser", "wrongpass");
//            System.out.println("Login fail: " + loginFail);

            // 5. List all users
            System.out.println("\n== All users ==");
            List<User> users = userService.getAllUsers();
            for(User u : users) {
            	System.out.print(u);
            	System.out.println(u.getBookings());
            }

//            // 6. Update user details
//            System.out.println("\n== Update user ==");
//            registered.setFirstname("NewFirst");
//            registered.setLastname("NewLast");
//            boolean updateResult = userService.updateUser(registered);
//            System.out.println("Update result: " + updateResult);
//            System.out.println("After update: " + userService.getUserById(registered.getId()));
    //
//            // 7. Delete user
//            System.out.println("\n== Delete user ==");
//            boolean deleteResult = userService.deleteUser(registered.getId());
//            System.out.println("Delete result: " + deleteResult);
//            System.out.println("After delete: " + userService.getUserById(registered.getId()));
    	}
    	catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
}
