package fm.douban.app.control;

import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SongTestControl {
    @Autowired
    private SongService songService;

    @GetMapping(path = "/test/song/add")
    public Song testAdd(){
        Song song = new Song();
        song.setId("0");
        song.setName("海阔天空");
        song.setLyrics("今天我，寒夜里看雪飘过");
        song.setCover("https://style.youkeda.com/img/ham/course/j13/j13-3-2-2.svg");
        song.setUrl("https://style.youkeda.com/img/ham/course/j13/j13-3-2-2.svg");
        return songService.add(song);
    }

    @GetMapping(path = "/test/song/get")
    public Song testGet(){
        return songService.get("0");
    }

    @GetMapping(path = "/test/song/list")
    public Page<Song> testList(){
        SongQueryParam songQueryParam = new SongQueryParam();
        songQueryParam.setName("海阔天空");
        return songService.list(songQueryParam);
    }
    @GetMapping(path = "/test/song/modify")
    public boolean testModify(){
        Song song = new Song();
        song.setId("0");
        song.setName("大海");
        return songService.modify(song);
    }

    @GetMapping(path = "/test/song/del")
    public boolean testDelete(){
        return songService.delete("0");
    }
}
