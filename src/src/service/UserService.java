package service;

import dao.UserDAO;
import entities.User;
import utils.PasswordUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    /**
     * Register a new user: hash password and insert.
     * @param user user object with plaintext password
     * @return created User or null if username/email exists
     */
    public User register(User user) throws RuntimeException{
        // check unique username/email
        List<User> existing = UserDAO.findAll();
        boolean exists = existing.stream().anyMatch(u ->
                u.getUsername().equals(user.getUsername()) ||
                u.getEmail().equals(user.getEmail())
        );
        if (exists) {
            throw new RuntimeException("Username or Email is existed");
        }
        String hashed = PasswordUtils.hashPassword(user.getPassword());
        user.setPassword(hashed);
        int rows = UserDAO.insert(user);
        return rows > 0 ? user : null;
    }

    /**
     * Authenticate user by username and plaintext password.
     * @return User if credentials match, else null
     */
    public User login(String username, String password) {
        List<User> users = UserDAO.findAll();
        Optional<User> opt = users.stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst();
        if (opt.isPresent()) {
            User u = opt.get();
            if (PasswordUtils.verifyPassword(password, u.getPassword())) {
                return u;
            }
        }
        else throw new RuntimeException("Username or password incorrect!");
        return null;
    }

    public User createUser(User user) {
        return register(user);
    }

    public User getUserById(UUID id) {
        User u =  UserDAO.findById(id);
        if(u == null) throw new RuntimeException("UserID not found!");
        return u;
    }

    public List<User> getAllUsers() {
        return UserDAO.findAll();
    }

    public List<User> searchByName(String key) {
        return UserDAO.findByName(key);
    }

    // incoming:
    public boolean updateUser(User user) {
        // if password changed, re-hash
        // assume caller sets already hashed if desired
        return UserDAO.update(user) > 0;
    }

    public boolean deleteUser(UUID id) {
        return UserDAO.delete(id) > 0;
    }
}