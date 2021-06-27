package sala.newsletter;

public interface Observer {
    boolean subscribe(Subject subject);

    String update(String news);
}
