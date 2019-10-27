package gdx.kapotopia.Game1;

public class VirusContainer {
    private String texturePath;
    private String name;
    private String description;
    private boolean isIst;

    public VirusContainer(String texturePath, String name, boolean isIst, String description) {
        this.texturePath = texturePath;
        this.name = name;
        this.description = description;
        this.isIst = isIst;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIst() {
        return isIst;
    }

    public void setIst(boolean ist) {
        isIst = ist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}