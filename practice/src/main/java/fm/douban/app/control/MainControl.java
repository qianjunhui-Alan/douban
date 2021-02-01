package fm.douban.app.control;

import fm.douban.model.*;
import fm.douban.param.SongQueryParam;
import fm.douban.service.FavoriteService;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.FavoriteUtil;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainControl {

    private static Logger logger = LoggerFactory.getLogger(MainControl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SubjectUtil subjectUtil;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private SongService songService;

    @Autowired
    private FavoriteUtil favoriteUtil;
    @GetMapping(path="/index")
    public String index(Model model){
       setSongData(model);
       setSubjectData(model);
        return "index";
    }

    @GetMapping(path = "/search")
        public String search(Model model){

        return "search";
    }

    @GetMapping(path = "/share")
    public String share(Model model) {
        setSongData(model);
        return "share";
    }


    @GetMapping(path = "/searchContent")
    @ResponseBody
    public Map searchContent(@RequestParam("keyword") String keyword){
        Map<String,List<Song>> resultsongs = new HashMap<>();
        SongQueryParam songQueryParam = new SongQueryParam();
        songQueryParam.setName(keyword);
        List<Song> songs = songService.list(songQueryParam).getContent();
        resultsongs.put("songs",songs);
        return resultsongs;
    }

    @GetMapping(path = "/my")
    public String myPage(Model model, HttpServletRequest request, HttpServletResponse response) {
        // 取得 HttpSession 对象
        HttpSession session = request.getSession();
        UserLoginInfo userLoginInfo = (UserLoginInfo)session.getAttribute("userLoginInfo");

        String userId = userLoginInfo.getUserId();

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setType(FavoriteUtil.TYPE_RED_HEART);
        List<Favorite> favs = favoriteService.list(fav);

        model.addAttribute("favorites", favs);

        List<Song> favedSongs = new ArrayList<>();
        if (favs != null && !favs.isEmpty()) {
            for (Favorite favorite : favs) {
                if (FavoriteUtil.TYPE_RED_HEART.equals(favorite.getType()) && FavoriteUtil.ITEM_TYPE_SONG.equals(
                        favorite.getItemType())) {
                    Song song = songService.get(favorite.getItemId());
                    if (song != null) {
                        favedSongs.add(song);
                    }
                }
            }
        }
        model.addAttribute("songs", favedSongs);
        setSongData(model);

        return "my";
    }

    @GetMapping(path = "/fav")
    @ResponseBody
    public Map doFav(@RequestParam(name = "itemType") String itemType, @RequestParam(name = "itemId") String itemId,
                     HttpServletRequest request, HttpServletResponse response) {
        Map resultData = new HashMap();
        // 取得 HttpSession 对象
        HttpSession session = request.getSession();
        UserLoginInfo userLoginInfo = (UserLoginInfo)session.getAttribute("userLoginInfo");
        String userId = userLoginInfo.getUserId();

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setType(FavoriteUtil.TYPE_RED_HEART);
        fav.setItemType(itemType);
        fav.setItemId(itemId);
        List<Favorite> favs = favoriteService.list(fav);
        if (favs == null || favs.isEmpty()) {
            favoriteService.add(fav);
        } else {
            for (Favorite f : favs) {
                favoriteService.delete(f);
            }
        }

        resultData.put("message", "successful");
        return resultData;
    }


    public void setSongData(Model model) {
        SongQueryParam songParam = new SongQueryParam();
        songParam.setPageNum(1);
        songParam.setPageSize(1);
        Page<Song> songs = songService.list(songParam);
        if (songs != null && !songs.isEmpty()) {
            Song resultSong = songs.getContent().get(0);
            model.addAttribute("song", resultSong);

            List<String> singerIds = resultSong.getSingerIds();

            List<Singer> singers = new ArrayList<>();
            if (singerIds != null && !singerIds.isEmpty()) {
                for (String singerId : singerIds) {
                    Singer singer = singerService.get(singerId);
                    singers.add(singer);
                }
            }
            model.addAttribute("singers", singers.get(0));

        }
    }

    public void setSubjectData(Model model){
        List<Subject> artistDatas = new ArrayList<>();
        List<Subject> moodDatas = new ArrayList<>();
        List<Subject> ageDatas = new ArrayList<>();
        List<Subject> styleDatas = new ArrayList<>();
        List<MhzViewModel> mhzViewModels = new ArrayList<>();

        List<Subject> subjects = subjectService.getSubjects(subjectUtil.TYPE_MHZ);
        for(Subject subjectData : subjects){
            if(subjectData.getSubjectSubType().equals(subjectUtil.TYPE_SUB_ARTIST)){
                artistDatas.add(subjectData);
            }
            if(subjectData.getSubjectSubType().equals(subjectUtil.TYPE_SUB_MOOD)){
                moodDatas.add(subjectData);

            }
            if(subjectData.getSubjectSubType().equals(subjectUtil.TYPE_SUB_AGE)){
                ageDatas.add(subjectData);

            }
            if(subjectData.getSubjectSubType().equals(subjectUtil.TYPE_SUB_STYLE)){
                styleDatas.add(subjectData);
            }
        }
        model.addAttribute("artistDatas",artistDatas);
        buildMhzViewModel("心情 / 场景" , moodDatas,mhzViewModels);
        buildMhzViewModel("语言 / 年代" , ageDatas , mhzViewModels);
        buildMhzViewModel("风格 / 流派" , styleDatas, mhzViewModels);
        model.addAttribute("mhzViewModels",mhzViewModels);

    }

    public void buildMhzViewModel(String title ,List<Subject> subjects,List<MhzViewModel> mhzViewModels){
        MhzViewModel mhzViewModel = new MhzViewModel();
        mhzViewModel.setTitle(title);
        mhzViewModel.setSubjects(subjects);
        mhzViewModels.add(mhzViewModel);
    }


}
