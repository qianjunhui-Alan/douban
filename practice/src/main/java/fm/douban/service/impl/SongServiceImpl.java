package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

@Service
public class SongServiceImpl implements SongService {
    private static final Logger LOG = LoggerFactory.getLogger(SingerServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public Song add(Song song) {
        if (song == null) {
            LOG.error("input song data is null.");
            return null;
        }
        return mongoTemplate.insert(song);
    }

    @Override
    public Song get(String songId) {
        if (!StringUtils.hasText(songId)) {
            LOG.error("input songId is blank.");
            return null;
        }

        Song song = mongoTemplate.findById(songId, Song.class);
        return song;
    }

    @Override
    public Page<Song> list(SongQueryParam songParam) {
        if(songParam == null){
            LOG.error("input song data is not correct.");
            return null;
        }
        Criteria criteria = new Criteria();
        List<Criteria> subCirs = new ArrayList<>();
        if(StringUtils.hasText(songParam.getName())){
            subCirs.add(Criteria.where("name").is(songParam.getName()));
        }
        if(StringUtils.hasText(songParam.getLyrics())){
            subCirs.add(Criteria.where("lyrics").is(songParam.getLyrics()));
        }
        if(StringUtils.hasText(songParam.getCover())){
            subCirs.add(Criteria.where("cover").is(songParam.getCover()));
        }
        if(StringUtils.hasText(songParam.getUrl())){
            subCirs.add(Criteria.where("url").is(songParam.getUrl()));
        }


        if (!subCirs.isEmpty()) {
            criteria.andOperator(subCirs.toArray(new Criteria[]{}));
        }

        Query query = new Query(criteria);
        long count = mongoTemplate.count(query,Song.class);
        Pageable pageable = PageRequest.of(songParam.getPageNum() - 1 ,songParam.getPageSize());
        query.with(pageable);
        List<Song> songs = mongoTemplate.find(query,Song.class);
        Page<Song> pageResult = PageableExecutionUtils.getPage(songs, pageable, new LongSupplier() {
            @Override
            public long getAsLong() {
                return count;
            }
        });

        return pageResult;




    }

    @Override
    public boolean modify(Song song) {
        if(song == null || !StringUtils.hasText(song.getId())){
            LOG.error("input song data is not correct.");
            return  false;
        }

        Query query = new Query(Criteria.where("id").is(song.getId()));
        Update update = new Update();
        if(song.getName()!=null){
            update.set("name" , song.getName());
        }
        if(song.getLyrics()!=null){
            update.set("lyrics" , song.getLyrics());
        }
        if(song.getCover()!=null){
            update.set("cover" , song.getCover());
        }
        if(song.getUrl()!=null){
            update.set("url" , song.getUrl());
        }
        if(song.getSingerIds()!=null){
            update.set("singerIds" , song.getSingerIds());
        }

        UpdateResult result = mongoTemplate.updateFirst(query,update,Song.class);
        return result != null && result.getModifiedCount() > 0;

    }

    @Override
    public boolean delete(String songId) {
        if(!StringUtils.hasText(songId)){
            LOG.error("input songId is blank.");
            return false;
        }
        Song song = new Song();
        song.setId(songId);

        DeleteResult result = mongoTemplate.remove(song);
        return result != null && result.getDeletedCount() > 0;
    }
}
