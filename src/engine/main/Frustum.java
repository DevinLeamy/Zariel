package engine.main;

import math.Vector3;

/**
 * View frustum.
 * ref: http://www.lighthouse3d.com/tutorials/view-frustum-culling/
 */
public class Frustum {
    // TODO: make this an enum and move it somewhere else
//    final public static int INSIDE = 1;
//    final public static int INTERSECT = 0;
//    final public static int OUTSIDE = -1;

    final private static int TOP = 0;
    final private static int BOTTOM = 1;
    final private static int LEFT = 2;
    final private static int RIGHT = 3;
    final private static int FRONT = 4;
    final private static int BACK = 5;
    Plane[] planes;
    /**
     * @param fov: horizontal field of view
     * @param ratio: width / height
     * @param position: frustum's "apex" position
     * @param lookAt: point the camera is looking at
     * @param trueUp: up vector in the world, typically the y axis (0, 1, 0)
     * @param nearPlaneDist: distance to the near plane
     * @param farPlaneDist: distance to the far plane
     */
    Frustum(float fov, float ratio, Vector3 position, Vector3 lookAt, Vector3 trueUp, float nearPlaneDist, float farPlaneDist) {
        // TODO: remove (hack to make more chunks render)
        fov += Math.PI / 4;

        // vector from position -> lookAt
        Vector3 direction = Vector3.sub(lookAt, position).normalize();
        trueUp.normalize();

        Vector3 right = Vector3.cross(direction, trueUp).normalize();
        Vector3 up = trueUp.clone(); // we'll assume they're the same

        float nearWidth = 2 * (float) Math.tan(fov / 2) * nearPlaneDist;
        float nearHeight = nearWidth / ratio;

        float farWidth = 2 * (float) Math.tan(fov / 2) * farPlaneDist;
        float farHeight = farWidth / ratio;

        /**
         * calculate frustum points
         * (near|far)(top|bottom)(left|right)
         * eg. fbl -> near-bottom-left point
         */
        Vector3 nearPlaneCenter = Vector3.add(position, Vector3.scale(direction, nearPlaneDist));
        Vector3 nearOffsetUp = Vector3.scale(up, 0.5f * nearHeight);
        Vector3 nearOffsetRight = Vector3.scale(right, 0.5f * nearWidth);

        Vector3 ntl = nearPlaneCenter.clone().sub(nearOffsetRight).add(nearOffsetUp);
        Vector3 nbl = nearPlaneCenter.clone().sub(nearOffsetRight).sub(nearOffsetUp);
        Vector3 ntr = nearPlaneCenter.clone().add(nearOffsetRight).add(nearOffsetUp);
        Vector3 nbr = nearPlaneCenter.clone().add(nearOffsetRight).sub(nearOffsetUp);

        Vector3 farPlaneCenter = Vector3.add(position, Vector3.scale(direction, farPlaneDist));
        Vector3 farOffsetUp = Vector3.scale(up, 0.5f * farHeight);
        Vector3 farOffsetRight = Vector3.scale(right, 0.5f * farWidth);

        Vector3 ftl = farPlaneCenter.clone().sub(farOffsetRight).add(farOffsetUp);
        Vector3 fbl = farPlaneCenter.clone().sub(farOffsetRight).sub(farOffsetUp);
        Vector3 ftr = farPlaneCenter.clone().add(farOffsetRight).add(farOffsetUp);
        Vector3 fbr = farPlaneCenter.clone().add(farOffsetRight).sub(farOffsetUp);

        /**
         * Define the 6 frustum planes. Planes are defined
         * such that their normals point into the view frustum.
         * Optimize calculations: http://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-extracting-the-planes/
         */

        planes = new Plane[6];
        planes[FRONT] = new Plane(ntr, nbr, ntl);
        planes[BACK] = new Plane(fbr, ftr, fbl);
        planes[LEFT] = new Plane(nbl, fbl, ntl);
        planes[RIGHT] = new Plane(ntr, ftr, nbr);
        planes[TOP] = new Plane(ntl, ftl, ntr);
        planes[BOTTOM] = new Plane(nbr, fbr, nbl);
    }

    public boolean pointInOrIntersectsFrustum(Vector3 point) {
        for (Plane plane : planes) {
            if (plane.signedDistance(point) < 0) {
                return false;
            }
        }

        return true;
    }

    /*
     * Not foolproof but pretty good.
     * ref: http://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes/
     */
    public boolean boxInOrIntersectsFrustum(BoundingBox box) {
        Vector3[] vertices = box.vertices();

        for (Plane plane : planes) {
            boolean allOutside = true;
            for (Vector3 vertex : vertices) {
                allOutside &= plane.signedDistance(vertex) < 0;
            }

            if (allOutside) {
                return false;
            }
        }

        return true;
    }
}
