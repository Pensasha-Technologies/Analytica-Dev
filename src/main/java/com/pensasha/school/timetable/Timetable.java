package com.pensasha.school.timetable;

import com.pensasha.school.form.Form;
import com.pensasha.school.school.School;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.term.Term;
import com.pensasha.school.year.Year;

import javax.persistence.*;

@Entity
public class Timetable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String day;
    private String time1;
    private String time2;
    private String time3;
    private String time4;
    private String time5;
    private String time6;
    private String time7;
    private String time8;
    private String time9;
    private String time10;
    private String time11;
    private String time12;
    private String time13;
    @ManyToOne
    private School school;
    @ManyToOne
    private Year year;
    @ManyToOne
    private Form form;
    @ManyToOne
    private Term term;
    @ManyToOne
    private Stream stream;

    public Timetable(School school, Year year, Form form, Term term, Stream stream) {
        this.school = school;
        this.year = year;
        this.form = form;
        this.term = term;
        this.stream = stream;
    }

    public Timetable(String day, String time1, String time2, String time3, String time4, String time5, String time6, String time7, String time8, String time9, String time10, String time11, String time12, String time13, School school, Year year, Form form, Term term, Stream stream) {
        this.day = day;
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        this.time4 = time4;
        this.time5 = time5;
        this.time6 = time6;
        this.time7 = time7;
        this.time8 = time8;
        this.time9 = time9;
        this.time10 = time10;
        this.time11 = time11;
        this.time12 = time12;
        this.time13 = time13;
        this.school = school;
        this.year = year;
        this.form = form;
        this.term = term;
        this.stream = stream;
    }

    public Timetable(int id, String day, String time1, String time2, String time3, String time4, String time5, String time6, String time7, String time8, String time9, String time10, String time11, String time12, String time13, School school, Year year, Form form, Term term, Stream stream) {
        this.id = id;
        this.day = day;
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        this.time4 = time4;
        this.time5 = time5;
        this.time6 = time6;
        this.time7 = time7;
        this.time8 = time8;
        this.time9 = time9;
        this.time10 = time10;
        this.time11 = time11;
        this.time12 = time12;
        this.time13 = time13;
        this.school = school;
        this.year = year;
        this.form = form;
        this.term = term;
        this.stream = stream;
    }

    public Timetable(String day, String time1, String time2, String time3, String time4, String time5, String time6, String time7, String time8, String time9, String time10, String time11, String time12, String time13) {
        this.day = day;
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        this.time4 = time4;
        this.time5 = time5;
        this.time6 = time6;
        this.time7 = time7;
        this.time8 = time8;
        this.time9 = time9;
        this.time10 = time10;
        this.time11 = time11;
        this.time12 = time12;
        this.time13 = time13;
    }

    public Timetable() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return this.day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime1() {
        return this.time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return this.time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getTime3() {
        return this.time3;
    }

    public void setTime3(String time3) {
        this.time3 = time3;
    }

    public String getTime4() {
        return this.time4;
    }

    public void setTime4(String time4) {
        this.time4 = time4;
    }

    public String getTime5() {
        return this.time5;
    }

    public void setTime5(String time5) {
        this.time5 = time5;
    }

    public String getTime6() {
        return this.time6;
    }

    public void setTime6(String time6) {
        this.time6 = time6;
    }

    public String getTime7() {
        return this.time7;
    }

    public void setTime7(String time7) {
        this.time7 = time7;
    }

    public String getTime8() {
        return this.time8;
    }

    public void setTime8(String time8) {
        this.time8 = time8;
    }

    public String getTime9() {
        return this.time9;
    }

    public void setTime9(String time9) {
        this.time9 = time9;
    }

    public String getTime10() {
        return this.time10;
    }

    public void setTime10(String time10) {
        this.time10 = time10;
    }

    public String getTime11() {
        return this.time11;
    }

    public void setTime11(String time11) {
        this.time11 = time11;
    }

    public String getTime12() {
        return this.time12;
    }

    public void setTime12(String time12) {
        this.time12 = time12;
    }

    public String getTime13() {
        return this.time13;
    }

    public void setTime13(String time13) {
        this.time13 = time13;
    }

    public School getSchool() {
        return this.school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Year getYear() {
        return this.year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public Form getForm() {
        return this.form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Term getTerm() {
        return this.term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public Stream getStream() {
        return this.stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }
}