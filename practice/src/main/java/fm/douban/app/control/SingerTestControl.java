package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class SingerTestControl {
    @Autowired
    private SingerService singerService;

    @GetMapping(path="/test/singer/add")
    public Singer testAddSinger(){
        Singer singer = new Singer();
        singer.setId("0");
        singer.setName("钱君辉");
        singer.setAvatar("https://style.youkeda.com/img/ham/course/j13/j13-2-4-3.svg");
        singer.setHomepage("12345679");
        return singerService.addSinger(singer);

    }
    @GetMapping(path="/test/singer/getAll")
    public List<Singer> testGetAll(){
        return singerService.getAll();

    }


    @GetMapping(path="/test/singer/getOne")
    public Singer testGetSinger(){
        return singerService.get("0");
    }


    @GetMapping(path="/test/singer/modify")
    public boolean testModifySinger(){
        Singer singer = new Singer();
        singer.setId("0");
        singer.setName("小钱无敌");
        return singerService.modify(singer);

    }
    @GetMapping(path="/test/singer/del")
    public boolean testDelSinger(){
        return singerService.delete("0");
    }
}
