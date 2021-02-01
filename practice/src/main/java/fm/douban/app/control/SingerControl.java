package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class SingerControl {

    @Autowired
    private SingerService singerService;
    @GetMapping(path = "/user-guide")
    public String myMhz(Model model){
        List<Singer> singers = randomSingers();
        model.addAttribute("singers",singers);
        return "userguide";
    }


    @GetMapping(path = "/singer/random")
    @ResponseBody
    public List<Singer> randomSingers(){
        List<Singer> singerList = singerService.getAll();
        List<Singer> singers = new ArrayList<>();
        for(int i = 0 ; i < 10 ; i++){
            Random r = new Random();
            int num = r.nextInt(singerList.size()-1);
            Singer singer = singerList.get(num);
            singers.add(singer);
        }
        return singers;
    }
}
