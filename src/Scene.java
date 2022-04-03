import java.util.ArrayList;

public class Scene {
    private Camera perspective;
    private ArrayList<Model> models;

    public Scene(Camera perspective) {
        this.perspective = perspective;
        this.models = new ArrayList<>();
    }


    public void addModel(Model model) {
        models.add(model);
    }

    public Camera getPerspective() {
        return perspective;
    }

    public ArrayList<Model> getModels() {
        return models;
    }
}
