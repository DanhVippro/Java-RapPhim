import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class TestHash {
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static void main(String[] args) {
        System.out.println("admin123: " + sha256("admin123"));
        System.out.println("nv123: " + sha256("nv123"));
    }
}
