//import org.lwjgl.BufferUtils;
//
//import java.nio.*;
//
//import static org.lwjgl.opengl.GL41.*;
//
//public class Square extends Model {
//    public Square(VertexShader vs, FragmentShader fs, Transform transform) {
//        super(vs, fs, transform);
//
//        this.vertexCount = 6;
//        this.coordinatesPerVertex = 3;
//        this.channelsPerColor = 3;
//
//        // initialize vertex buffer
//        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(
//                this.vertexCount * (this.coordinatesPerVertex + this.channelsPerColor)
//        );
//        // (x,y,z,R,G,B)
//        // Triangle 1
//        vertexBuffer.put(new float[] { 0f, 0.5f, 0f, 0f, 1.0f, 0f });
//        vertexBuffer.put(new float[] { 0.5f, 0f, 0f, 1.0f, 0f, 0f });
//        vertexBuffer.put(new float[] { -0.5f, 0f, 0f, 0f, 0f, 1.0f });
//        // Triangle 2
//        vertexBuffer.put(new float[] { 0f, -0.5f, 0f, 0f, 1.0f, 0f });
//        vertexBuffer.put(new float[] { 0.5f, 0f, 0f, 1.0f, 0f, 0f });
//        vertexBuffer.put(new float[] { -0.5f, 0f, 0f, 0f, 0f, 1.0f });
//        vertexBuffer.flip();
//
//        this.vao = glGenVertexArrays();
//        this.vbo = glGenBuffers();
//
//
//        glBindVertexArray(this.vao);
//        // load vertex buffer data
//        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
//        // GL_STATIC_DRAW => Data is modified once and used many times
//        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
//        // tell OpenGL how to read the buffer
//
//        int stride = (this.coordinatesPerVertex + this.channelsPerColor) * 4;
//        glVertexAttribPointer(0, this.coordinatesPerVertex, GL_FLOAT, false, stride, 0);
//        glVertexAttribPointer(1, this.channelsPerColor, GL_FLOAT, false, stride, this.coordinatesPerVertex * 4L);
//
//        // "yes, we want to use attribute 0"
//        glEnableVertexAttribArray(0);
//        glEnableVertexAttribArray(1);
//
//        // cleanup
//        glBindVertexArray(0);
//    }
//
//    @Override
//    public void update(float dt) {
//        transform.rotate(0.00f, 0.00f, 0.01f);
//    }
//
//    @Override
//    protected void setUniforms() {
//        int rotationMHandler = glGetUniformLocation(shaderProgram.getProgramHandle(),  "rotationM");
//        glUniformMatrix3fv(rotationMHandler, false, transform.rotationMatrix().toFloatBuffer());
//    }
//}
