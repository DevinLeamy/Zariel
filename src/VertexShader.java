public class VertexShader extends Shader {
    public VertexShader(String shaderPath) {
        super(shaderPath);

        source =
          """
              #version 430 core
              
              layout(location = 0) in vec3 vPosition;
              
              void main()
              {
                  gl_Position = vec4(vPosition, 1.0);
              }
          """;
    }
}
