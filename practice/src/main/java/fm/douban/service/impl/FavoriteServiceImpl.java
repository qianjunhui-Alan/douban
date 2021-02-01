package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import fm.douban.model.Favorite;
import fm.douban.model.Subject;
import fm.douban.service.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Favorite add(Favorite fav) {
        if(fav == null){
            LOG.error("input fav data is null.");
            return null;
        }
        return mongoTemplate.insert(fav);
    }

    @Override
    public List<Favorite> list(Favorite favParam) {
        if (favParam == null) {
            LOG.error("input favParam is not correct.");
            return null;
        }
        Criteria criteria = new Criteria();
        List<Criteria> subCirs = new ArrayList<>();
        if(StringUtils.hasText(favParam.getItemId())){
            subCirs.add(Criteria.where("itemId").is(favParam.getItemId()));
        }
        if(StringUtils.hasText(favParam.getId())){
            subCirs.add(Criteria.where("itemType").is(favParam.getItemType()));
        }
        if(StringUtils.hasText(favParam.getType())){
            subCirs.add(Criteria.where("type").is(favParam.getType()));
        }
        if(StringUtils.hasText(favParam.getUserId())){
            subCirs.add(Criteria.where("userId").is(favParam.getUserId()));
        }


        if (!subCirs.isEmpty()) {
            criteria.andOperator(subCirs.toArray(new Criteria[]{}));
        }

        Query query = new Query(criteria);
        List<Favorite> favorites = mongoTemplate.find(query, Favorite.class);
        return favorites;
    }


    @Override
    public boolean delete(Favorite favParam) {
        if (favParam == null) {
            return false;
        }

        DeleteResult result = mongoTemplate.remove(favParam);
        return result != null && result.getDeletedCount() > 0;
    }
}
