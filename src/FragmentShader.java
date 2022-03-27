public class FragmentShader extends Shader {
    public FragmentShader(String shaderPath) {
        super(shaderPath);

        source =
                """
                    #version 430 core
                    
                    layout(location = 0) in vec3 vColor;
                    
                    void main()
                    {
                        gl_FragColor = vec4(vColor, 1.0);
                    }
                """;
    }
}
