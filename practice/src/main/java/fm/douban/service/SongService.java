package fm.douban.service;

import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import org.springframework.data.domain.Page;

public interface SongService {
    public Song add(Song song);
    public Song get(String songId);
    public Page<Song> list(SongQueryParam songParam);
    public boolean modify(Song song);
    public boolean delete(String songId);
}
