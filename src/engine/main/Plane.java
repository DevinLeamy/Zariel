package engine.main;

import math.Vector3;

/**
 * reference:
 * http://www.lighthouse3d.com/tutorials/maths/plane/
 * https://math.stackexchange.com/questions/895035/dot-product-of-any-point-on-plane-and-its-normal
 */
public class Plane {
    Vector3 normal;
    float d;

    /**
     * We define the normal of our plane to be pointing up
     * on the triangle p0-p2-p1.
     */
    public Plane(Vector3 p0, Vector3 p1, Vector3 p2) {
        Vector3 u = Vector3.sub(p2, p0);
        Vector3 v = Vector3.sub(p1, p0);
        normal = Vector3.cross(v, u).normalize();

        d = -Vector3.dot(normal, p0);
    }

    /**
     * Distance from a point to the plane.
     * signedDistance(point) >  0, point is on the side that "agrees" with the normal
     * signedDistance(point) == 0, point is on the plane
     * signedDistance(point) <  0, point is on the side that "disagrees" with the normal
     */
    public float signedDistance(Vector3 point) {
        return Vector3.dot(normal, point) + d;
    }

    public float distance(Vector3 point) {
        return Math.abs(signedDistance(point));
    }
}
