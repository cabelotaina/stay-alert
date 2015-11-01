package org.blackbird.stayalert;

public class Tag {

    private int _id;
    private String _name;
    private int _number;


    public Tag(int id, String name, int number) {
        _id = id;
        _name = name;
        _number = number;
    }

    public int id(){
        return _id;
    }

    public String name(){
        return _name;
    }

    public int number(){
        return _number;
    }
}
