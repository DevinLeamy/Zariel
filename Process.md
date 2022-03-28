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



# OpenGL

### Lessons
- Core OpenGL __requires__ that you use VAOs
- `GL_FLOAT` is not the size of a float!
- VAOs store the "configuration" of VBOs - nothing else

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


