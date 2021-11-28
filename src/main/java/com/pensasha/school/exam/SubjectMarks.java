package com.pensasha.school.exam;

public class SubjectMarks {

    private String name;
    private int mark;

    public SubjectMarks(String name, int mark) {
        this.name = name;
        this.mark = mark;
    }

    public SubjectMarks() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

}
