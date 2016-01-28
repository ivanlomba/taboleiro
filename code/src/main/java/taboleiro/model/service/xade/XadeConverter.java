package taboleiro.model.service.xade;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import taboleiro.model.domain.course.GroupSubject;
import taboleiro.model.domain.course.ClassGroup;
import taboleiro.model.domain.student.GlobalGrade;
import taboleiro.model.domain.student.Student;
import taboleiro.model.exception.GroupSubjectNotFoundException;
import taboleiro.model.exception.StudentNotFoundException;
import taboleiro.model.service.StudentService;

@Component
public class XadeConverter {

    @Autowired
    private StudentService studentService;

    public Xade groupSubjectToXade(GroupSubject groupSubject, GlobalGrade.Evaluation evaluation)
            throws StudentNotFoundException, GroupSubjectNotFoundException {
        ClassGroup cg = groupSubject.getClassGroup();
        Set<Student> studentList = cg.getStudentList();
        List<GlobalGrade> gradeList = studentService.findGlobalGradeByGroupSubjectAndEvaluation(
                groupSubject.getGroupSubjectId(), evaluation);
        List<XadeStudent> xadeStudentList = new ArrayList<XadeStudent>();
        Iterator<Student> iterator = studentList.iterator();
        while(iterator.hasNext()) {
            Student studentListElement = iterator.next();
            Iterator<GlobalGrade> gradeIterator = gradeList.iterator();
            String grade = "";
            while(gradeIterator.hasNext()){
                GlobalGrade gradeListElement = gradeIterator.next();
                if(gradeListElement.getStudent().getStudentId().equals(studentListElement.getStudentId())) {
                    grade = gradeListElement.getGrade().getXadeGrade();
                }
            }
            xadeStudentList.add(XadeStudent.builder().studentCode(studentListElement.getCodigoAlumno())
                    .gradeCode(grade)
                    .studentName(studentListElement.getLastName() + ", " + studentListElement.getFirstName())
                    .build());
        }
        if (xadeStudentList.isEmpty()) {
            throw new StudentNotFoundException(xadeStudentList, "Student list empty");
        }
        else {
            return Xade.builder()
                    .groupCode(groupSubject.getClassGroup().getCodigoGrupo())
                    .evaluation(evaluation.toString())
                    .codigoMateriaAvaliable(groupSubject.getSubject().getCodigoMateriaAvaliable())
                    .xadeStudents(xadeStudentList)
                    .build();
        }
    }
}
