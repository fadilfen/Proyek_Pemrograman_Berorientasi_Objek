package tubes_a11;

import java.util.HashMap;

public class UserManager {
    private static HashMap<String, UserData> registeredUsers = new HashMap<>();
    
    static {
        registeredUsers.put("admin", new UserData("Admin Operator", "12345"));
    }
    
    static class UserData {
        String namaLengkap;
        String password;
        
        UserData(String namaLengkap, String password) {
            this.namaLengkap = namaLengkap;
            this.password = password;
        }
    }
    
    public static boolean register(String username, String password, String namaLengkap) {
        if (registeredUsers.containsKey(username)) {
            return false;
        }
        registeredUsers.put(username, new UserData(namaLengkap, password));
        return true;
    }
    
    public static String login(String username, String password) {
        UserData data = registeredUsers.get(username);
        if (data != null && data.password.equals(password)) {
            return data.namaLengkap;
        }
        return null;
    }
    
    public static boolean updateCredentials(String oldUsername, String newUsername, String newPassword) {
        if (!registeredUsers.containsKey(oldUsername)) {
            return false;
        }
        if (!oldUsername.equals(newUsername) && registeredUsers.containsKey(newUsername)) {
            return false;
        }
        UserData data = registeredUsers.get(oldUsername);
        registeredUsers.remove(oldUsername);
        data.password = newPassword;
        registeredUsers.put(newUsername, data);
        return true;
    }
}
