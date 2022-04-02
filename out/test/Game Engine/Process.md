# Zariel 

First thing I needed to do was install the latest Java JDK
and with IntelliJ IDEA. 

Next, I installed LWJGL. LWJGL contains Java language bindings for OpenGL and 
GLFW, two APIs commonly used for OpenGL development. LWJGL affectively lets you
use OpenGL within Java.


LWJGL Version:  3.3.1
GLSL  Version:  4.10
OpenGL Version: 4.1

### Day 1
- Created a number of boilerplate files.
- Got a triangle rendering to the screen.
- Created a Trello board for the project.

### Day 2
- Reading from shaders
- Added some classes
- Moved to `OpenGL 4.10` and `GLSL 4.1`
- Rendering using shaders
- Rendering using VAO
- Rendering color and position
- Creating a rainbow rotating triangle

### Day 3
- Created a `Square`
- Started a `math` package 
- Created a loader for Wavefront `.obj` files

### March 31st 
- View matrix
- Perspective projection
- 4D matrices for 3D affine transformations




# OpenGL

### Lessons
- Core OpenGL __requires__ that you use VAOs
- `GL_FLOAT` is not the size of a float!
- VAOs store the "configuration" of VBOs and EBOs - nothing else
- EBO (Element Buffer Objects) are just lists of indices for vertices
- OpenGl makes you copy normals and texture coords! You can only specify "vertex" indices
  but the different vertices many use the same texture coords or vertex normals -> repeated data!
- OpenGL reads matrix uniforms in `column-major` format my default! Sigh... Remember to transpose your matrices folks.

### GLSL Basics

```glsl
#version version_number
in type in_variable_name;
in type in_variable_name;

out type out_variable_name;
  
uniform type uniform_name;
  
void main()
{
  // process input(s) and do some weird graphics stuff
  ...
  // output processed stuff to output variable
  out_variable_name = weird_stuff_we_processed;
}
```


### Resources
- https://learnopengl.com/
- https://docs.gl
- https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/shading-normals
- https://docs.unity3d.com/Packages/com.unity.probuilder@4.0/manual/workflow-texture-mapping.html#:~:text=The%20Mesh%20stores%20the%20texture,specific%20locations%20on%20the%20image.
- https://ogldev.org/www/tutorial13/tutorial13.html 
- http://www.songho.ca/opengl/gl_transform.html#modelview 
