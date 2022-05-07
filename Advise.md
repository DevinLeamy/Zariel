1. Don't create components (or entities) during one update of the systems that are
intended to be used in a system, down the pipeline, later in the same update.
Components and entities that are created by systems should be created only AFTER
all the systems have finished running. This avoids problems where systems need to 
run in a very specific order. 
2. Physics systems should report and resolve all collisions between rigid body entities.
They should not have to know WHO collided with WHAT, just that a collision between X and Y
occurred. It is them up to a collision handler to determine, based on the collisions that have
been reported and resolved by the physics system, what the impact (on components/entities) should
be of the observed collision.
3. Systems should not be able to directly access all of the `World` game state. That's just too much power.
However, systems need to, at time, create new entities and access and update fields in the global game 
state. There are two problems here: reading and writing.
    - To write, consider implementing the Command design pattern, and collecting a queue of commands, which are 
later executed after the systems have finished running.
    - To read, find a way to allow specific system subclasses to access specific global resources. 


