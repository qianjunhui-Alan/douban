package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SingerServiceImpl implements SingerService {
    private static final Logger LOG = LoggerFactory.getLogger(SingerServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Singer addSinger(Singer singer) {
        if (singer == null) {
            LOG.error("input singer data is null.");
            return null;
        }
        return mongoTemplate.insert(singer);
    }

    @Override
    public Singer get(String singerId) {
        if (!StringUtils.hasText(singerId)) {
            LOG.error("input songId is blank.");
            return null;
        }

        Singer singer = mongoTemplate.findById(singerId, Singer.class);
        return singer;
    }

    @Override
    public List<Singer> getAll() {

        return mongoTemplate.findAll(Singer.class);

    }

    @Override
    public boolean modify(Singer singer) {
        if(singer == null || !StringUtils.hasText(singer.getId())){
            LOG.error("input singer data is not correct.");
            return  false;
        }
        Query query = new Query(Criteria.where("id").is(singer.getId()));
        Update update= new Update();
        if(singer.getName() != null){
            update.set("name" , singer.getName());
        }
        if(singer.getAvatar() != null){
            update.set("avatar" , singer.getAvatar());
        }
        if(singer.getHomepage() != null){
            update.set("homepage" , singer.getHomepage());
        }
        if(singer.getSimilarSingerIds() != null){
            update.set("similarSingerIds" , singer.getSimilarSingerIds());
        }

        UpdateResult result = mongoTemplate.updateFirst(query,update,Singer.class);
        return result != null && result.getModifiedCount() > 0;


    }

    @Override
    public boolean delete(String singerId) {
        if(!StringUtils.hasText(singerId)){
            LOG.error("input singerId is blank.");
            return false;
        }
        Singer singer = new Singer();
        singer.setId(singerId);
        DeleteResult result = mongoTemplate.remove(singer);
        return result != null && result.getDeletedCount() > 0;
    }
}
