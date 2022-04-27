package engine.main;

import math.Matrix3;
import math.Vector3;

public class Sun {
    final private static float ROTATION_SPEED = (float) 0.1;
    private Vector3 direction;

    public Sun() {
        this.direction = new Vector3(-1.0f, 0, 0);
    }


    public void update(float dt) {
        float magnitude = (float) (ROTATION_SPEED * Math.PI);
        direction = Matrix3.mult(Matrix3.genRotationMatrix(0, 0, magnitude), direction);


        if (this.direction.x > 1.0f) {
            this.direction.x = 1.0f;
        }
    }
}
