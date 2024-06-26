import java.util.ArrayList;
import java.util.List;
public class Node {
    private String type;
    private String name;
    private String value;
    private List<Node> children;

    Node()  {
        children = new ArrayList<>();
    }
    Node (String type, String name, String value){
        this();
        this.type = type;
        this.name = name;
        this.value = value;

    }// end Node Constructor

    public void addChilredn(Node child){
        children.add(child);
    }

    public Node getChild(int i){
        return children.get(i);
    }
    public int size(){
        return children.size();
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }

}// end Node class
