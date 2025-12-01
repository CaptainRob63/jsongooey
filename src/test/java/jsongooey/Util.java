package jsongooey;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Util {

    public static String readResourceToString(String fileName) throws IOException {
        try (InputStream is = Util.class.getResourceAsStream(fileName)) {
            if (is == null) throw new FileNotFoundException(fileName);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
