package fm.douban.app.control;

import fm.douban.model.Subject;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class SubjectTestControl {


    @Autowired
    private SubjectService subjectService;

    @GetMapping(path="/test/subject/add")
    public  Subject testAdd(){
        SubjectUtil subjectUtil = new SubjectUtil();
        Subject subject = new Subject();
        subject.setId("0");
        subject.setCover("https://style.youkeda.com/img/ham/course/j13/j13-3-6-1.svg");
        subject.setDescription("我爱你");
        subject.setMaster("钱君辉");
        subject.setName("爱情是如何发展的");
        subject.setSubjectType(subjectUtil.TYPE_MHZ);
        subject.setSubjectSubType(subjectUtil.TYPE_SUB_MOOD);
        return subjectService.addSubject(subject);
    }

    @GetMapping(path="/test/subject/get")
    public  Subject testGet(){
        return subjectService.get("0");

    }
    @GetMapping(path="/test/subject/getByType")
    public List<Subject> testGetByType(){
        SubjectUtil subjectUtil = new SubjectUtil();

        return subjectService.getSubjects(subjectUtil.TYPE_MHZ);

    }
    @GetMapping(path="/test/subject/getBySubType")
    public List<Subject> testGetBySubType(){
        SubjectUtil subjectUtil = new SubjectUtil();
        return subjectService.getSubjects(subjectUtil.TYPE_MHZ,subjectUtil.TYPE_SUB_MOOD);
    }
    @GetMapping(path="/test/subject/del")
    public  boolean testDelete(){
        return subjectService.delete("0");
    }
}

