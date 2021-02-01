package fm.douban.service;

import fm.douban.model.Singer;
import fm.douban.model.Subject;

import java.util.List;

public interface SubjectService {
    public Subject addSubject(Subject subject);
    public Subject get(String subjectId);
    public List<Subject> getSubjects(Subject subjectParam);
    public List<Subject> getSubjects(String type);
    public List<Subject> getSubjects(String type , String subType);
    public boolean delete(String subjectId);
    public List<Subject> getAll();
    public boolean modify(Subject subject);

}
