import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Shader {
    protected String source;
    public Shader(String shaderPath) {
        source = loadSource(shaderPath);


    }

    private String loadSource(String path) {
        String source = "";
        try {
            source = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            System.err.println("Error: loading shader (" + path + ")");
        }

        return source;
    }
}
