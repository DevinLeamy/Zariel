package engine.main;

import engine.World;
import engine.components.PlayerTag;
import engine.components.Transform;
import engine.ecs.ComponentRegistry;
import org.lwjgl.Version;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL41.*;

final public class Leamer {
    final static private int FRAMES_PER_SECOND = 60;
    final static private float SECONDS_PER_FRAME = 1f / FRAMES_PER_SECOND;
    final static private long MILLISECONDS_PER_FRAME = (long) (SECONDS_PER_FRAME * 1000f);
    private static boolean running = true;

    public static void main(String[] args) {
        World.init();

        // versions
        System.out.println("LWJGL_VERSION: " + Version.getVersion());
        System.out.println("GL_SHADING_LANGUAGE_VERSION: " + glGetString (GL_SHADING_LANGUAGE_VERSION));
        System.out.println("OPEN_GL_VERSION: " + glGetString (GL_VERSION));

        // stores the duration of each frame in seconds
        ArrayList<Long> frames = new ArrayList<>();

        long prevTime = System.currentTimeMillis();
        while (running) {
            long prevEndTime = prevTime;
            long dtInMillis = System.currentTimeMillis() - prevEndTime;
            float dt = dtInMillis / 1000f;
            prevTime += dtInMillis;

            // updates
            World.update(dt);
            World.render();

            // waiting
            try {
                long updateTime = System.currentTimeMillis() - prevEndTime;
                long waitTime = Long.max(0L, MILLISECONDS_PER_FRAME - updateTime);
                Thread.sleep(waitTime);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            // update frame count
            long trueDt = System.currentTimeMillis() - prevEndTime;
            frames.add(trueDt);

            long framesDuration = frames.stream().mapToLong(Long::longValue).sum();
            if (framesDuration >= 1000) {
                try {
                    World.window.setTitle(String.format(
                                    "FPS: %d POS: %s",
//                        "CHUNK: %s",
                                    frames.size()
                                    ,
                                    World.entityManager.queryEntities(ComponentRegistry.getSignature(Transform.class, PlayerTag.class)).get(0).getComponent(Transform.class).get().position)
                    );
                } catch (Exception e) {}

            }

            while (framesDuration >= 1000) {
                framesDuration -= frames.get(0);
                frames.remove(0);
            }
        }

        World.window.cleanUp();
    }
}