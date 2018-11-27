package testjdbc;

import org.junit.Test;

import java.util.Objects;
import java.util.Scanner;

public class Student {
    private int flowId;
    private int type;
    private String idCard;
    private String examCard;
    private String studentName;
    private String location;
    private int grade;

    public Student() {
    }

    @Test
    public void test() {
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        System.out.println(str);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return flowId == student.flowId &&
                type == student.type &&
                grade == student.grade &&
                Objects.equals(idCard, student.idCard) &&
                Objects.equals(examCard, student.examCard) &&
                Objects.equals(studentName, student.studentName) &&
                Objects.equals(location, student.location);
    }


    @Override
    public int hashCode() {
        return Objects.hash(flowId, type, idCard, examCard, studentName, location, grade);
    }

    @Override
    public String toString() {
        return "Student{" +
                "flowId=" + flowId +
                ", type=" + type +
                ", idCard='" + idCard + '\'' +
                ", examCard='" + examCard + '\'' +
                ", studentName='" + studentName + '\'' +
                ", location='" + location + '\'' +
                ", grade=" + grade +
                '}';
    }

    public Student(int flowId, int type, String idCard, String examCard, String studentName, String location, int grade) {
        this.flowId = flowId;
        this.type = type;
        this.idCard = idCard;
        this.examCard = examCard;
        this.studentName = studentName;
        this.location = location;
        this.grade = grade;
    }

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getExamCard() {
        return examCard;
    }

    public void setExamCard(String examCard) {
        this.examCard = examCard;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
