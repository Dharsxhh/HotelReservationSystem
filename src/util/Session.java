package util;

import model.User;

public final class Session {
    private static User currentUser;

    private Session() { }
    public static void login(User user) { currentUser = user; }
    public static User current() { return currentUser; }
    public static boolean isLoggedIn() { return currentUser != null; }
    public static boolean isAdmin() { return currentUser != null && currentUser.isAdmin(); }
    public static void logout() { currentUser = null; }
}
