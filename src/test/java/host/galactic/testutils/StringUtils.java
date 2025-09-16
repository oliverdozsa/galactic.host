package host.galactic.testutils;

import java.util.Random;

public class StringUtils {
    public static String generateRandomStringOfLength(int value) {
        var abc = "abcdefghijklmnopqrstuvxyz";
        var random = new Random();

        var stringBuilder = new StringBuilder();
        for(int i = 0; i < value; i++) {
            int randomIndex = random.nextInt(abc.length());
            char randomLetter = abc.charAt(randomIndex);
            stringBuilder.append(randomLetter);
        }

        return stringBuilder.toString();
    }
}
