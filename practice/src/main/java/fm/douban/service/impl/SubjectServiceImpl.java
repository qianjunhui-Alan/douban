package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {
    private static final Logger LOG = LoggerFactory.getLogger(SingerServiceImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Subject addSubject(Subject subject) {
        if (subject == null) {
            LOG.error("input subject data is null.");
            return null;
        }
        return mongoTemplate.insert(subject);
    }

    @Override
    public Subject get(String subjectId) {
        if (!StringUtils.hasText(subjectId)) {
            LOG.error("input subjectId is blank.");
            return null;
        }

        Subject subject = mongoTemplate.findById(subjectId, Subject.class);
        return subject;
    }


    @Override
    public List<Subject> getSubjects(Subject subjectParam) {
        if (subjectParam == null) {
            LOG.error("input subjectParam is not correct.");
            return null;
        }

        Query query = new Query(Criteria.where("master").is(subjectParam.getMaster()));
        List<Subject> subjects = mongoTemplate.find(query,Subject.class);
        return subjects;
    }

    @Override
    public List<Subject> getSubjects(String type) {
        if (type == null){
            LOG.error("input type data is not correct.");
            return null;
        }

        Query query = new Query(Criteria.where("subjectType").is(type));
        List<Subject> subjects = mongoTemplate.find(query,Subject.class);
        return subjects;

    }

    @Override
    public List<Subject> getSubjects(String type, String subType) {
        if (type == null || subType == null){
            LOG.error("input type data is not correct.");
            return null;
        }
        Criteria criteria = new Criteria();
        Criteria criteria1 = Criteria.where("subjectType").is(type);
        Criteria criteria2 = Criteria.where("subjectSubType").is(subType);
        criteria.andOperator(criteria1,criteria2);
        Query query = new Query(criteria);
        List<Subject> subjects = mongoTemplate.find(query,Subject.class);
        return subjects;

    }

    @Override
    public boolean delete(String subjectId) {
        if(!StringUtils.hasText(subjectId)){
            LOG.error("input subjectId is blank.");
            return false;
        }
        Subject subject = new Subject();
        subject.setId(subjectId);

        DeleteResult result = mongoTemplate.remove(subject);
        return result != null && result.getDeletedCount() > 0;
    }

    @Override
    public List<Subject> getAll() {
        return mongoTemplate.findAll(Subject.class);
    }

    public boolean modify(Subject subject) {
        // 作为服务，要对入参进行判断，不能假设被调用时，入参一定正确
        if (subject == null || !StringUtils.hasText(subject.getId())) {
            LOG.error("input song data is not correct.");
            return false;
        }

        // 主键不能修改，作为查询条件
        Query query = new Query(Criteria.where("id").is(subject.getId()));

        Update updateData = new Update();
        // 每次修改都更新 gmtModified 为当前时间
        updateData.set("gmtModified", LocalDateTime.now());
        // 值为 null 表示不修改。值为长度为 0 的字符串 "" 表示清空此字段
        if (subject.getSongIds() != null) {
            updateData.set("songIds", subject.getSongIds());
        }

        // 把一条符合条件的记录，修改其字段
        UpdateResult result = mongoTemplate.updateFirst(query, updateData, Subject.class);
        return result != null && result.getModifiedCount() > 0;
    }
}
