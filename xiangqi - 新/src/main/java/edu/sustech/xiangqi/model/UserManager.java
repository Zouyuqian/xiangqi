package edu.sustech.xiangqi.model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USER_DATA_FILE = "users.dat";
    private Map<String, User> users;
    private User currentUser;

    public UserManager() {
        users = new HashMap<>();
        loadUsers();
        createGuestUser();
    }

    private void createGuestUser() {
        users.put("guest", new User("guest", "", true));
    }

    public boolean registerUser(String username, String password) {
        if (users.containsKey(username) || username.equals("guest")) {
            return false;
        }
        users.put(username, new User(username, password, false));
        saveUsers();
        return true;
    }

    public boolean loginUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void loginAsGuest() {
        currentUser = users.get("guest");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_DATA_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No existing user data found, starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCurrentUser() {
        saveUsers();
    }
}