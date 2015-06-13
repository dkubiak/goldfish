package pl.daku.goldfish.server.model;

public class FooModel {

    private final long id;
    private final String content;

    public FooModel(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
