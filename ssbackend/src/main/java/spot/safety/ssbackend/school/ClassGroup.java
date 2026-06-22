package spot.safety.ssbackend.school;

import jakarta.persistence.*;
import lombok.Data;
import spot.safety.ssbackend.user.Student;
import spot.safety.ssbackend.user.Teacher;

import java.util.List;

@Entity
@Data
public class ClassGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    String name;

    @ManyToOne
    School school;

    @ManyToMany
    @JoinTable(
            name = "classgroup_students",
            joinColumns = @JoinColumn(name = "classgroup_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    List<Student> studentList;

    @ManyToOne
    Teacher teacher;

    public ClassGroup(String name, School school, List<Student> studentList, Teacher teacher) {
        this.name = name;
        this.school = school;
        this.studentList = studentList;
        this.teacher = teacher;
    }

    public ClassGroup() {

    }

    public void addStudent(Student student) {
        this.studentList.add(student);
    }

    public void removeStudent(Student student) {
        this.studentList.remove(student);
    }
}
