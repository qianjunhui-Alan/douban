package fm.douban.app.control;

import fm.douban.model.Data;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SubjectControl {


    private static Logger logger = LoggerFactory.getLogger(SubjectControl.class);
    @Autowired
    private SubjectUtil subjectUtil;

    @Autowired
    private MainControl mainControl;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SongService songService;

    @Autowired
    private SubjectService subjectService;



    @GetMapping(path = "/artist")
    public String mhzDetail(Model model, @RequestParam("subjectId") String subjectId){
        Subject subject = subjectService.get(subjectId);
        List<String> songIds = subject.getSongIds();
        List<Song> songs = buildsongList(songIds);
        Singer singer = buildSinger(songs);
        List<Singer> simSingers = buildSimSingers(singer);
        model.addAttribute("subject" , subject);
        model.addAttribute("songs" , songs);
        model.addAttribute("singer" , singer);
        model.addAttribute("simSingers" , simSingers);
        mainControl.setSongData(model);
        return "mhzdetail";
    }


    @GetMapping(path = "/collection")
    public String collection(Model model){
        List<Subject> subjects = subjectService.getSubjects(subjectUtil.TYPE_COLLECTION);
        Map<String , List<Data>> datasList = new HashMap<>();
        for(Subject subject : subjects){
            String masterId = subject.getMaster();
            Singer master = singerService.get(masterId);
            subject.setMastername(master.getName());
            List<String> songIds = subject.getSongIds();
            List<Song> songs = buildsongList(songIds);
            List<Data> dataList = buildDataList(songs);
            datasList.put(subject.getId(),dataList);
        }
        model.addAttribute("subjects" , subjects);
        model.addAttribute("datasList" , datasList);
        mainControl.setSongData(model);
        return "collection";
    }


    @GetMapping(path = "collectiondetail")
    public String collectionDetail(Model model,@RequestParam("subjectId") String subjectId){

        Subject subject = subjectService.get(subjectId);
        List<Subject> subjects = subjectService.getSubjects(subjectUtil.TYPE_COLLECTION);
        for(Subject subject1 : subjects) {
            if(subject.getId().equals(subject1.getId())){
                String masterId = subject1.getMaster();
                Singer master = singerService.get(masterId);
                subject.setMastername(master.getName());
            }

        }
        List<Subject> otherSubjects = subjectService.getSubjects(subject);
        for(Subject subjectdata : otherSubjects){
            String masterId = subjectdata.getMaster();
            Singer master = singerService.get(masterId);
            subjectdata.setMastername(master.getName());
        }

        List<String> songIds = subject.getSongIds();
        List<Song> songs = buildsongList(songIds);
        Singer singer = buildSinger(songs);
        List<Data> dataList = buildDataList(songs);
        model.addAttribute("subject" , subject);
        model.addAttribute("otherSubjects" , otherSubjects);
        //model.addAttribute("singer" , singer);
       // model.addAttribute("songs" , songs);
        model.addAttribute("dataList" , dataList);
        mainControl.setSongData(model);

        return "collectiondetail";

    }



    public List<Song> buildsongList(List<String> songIds){
        List<Song> songs = new ArrayList<>();
        if(songIds == null){
            logger.info("songids为空");
            return songs;
        }
        for(String songId : songIds){
            Song song = songService.get(songId);
            songs.add(song);
        }
        return songs;
    }



    public Singer buildSinger(List<Song> songs){
        if (songs == null || songs.isEmpty()) {
            logger.info("songs为空");
            return null;
        }
        Song song = songs.get(0);
        String singerId = song.getSingerIds().get(0);
        Singer singer = singerService.get(singerId);
        return singer;
    }

    public List<Data> buildDataList(List<Song> songs){
        List<Data> dataList = new ArrayList<>();
        if (songs == null || songs.isEmpty()) {
            logger.info("songs为空");
            return null;
        }

        for (Song song : songs){
            Data data = new Data();
            String singerId = song.getSingerIds().get(0);

            Singer singer = singerService.get(singerId);
            data.setSingers(singer);
            data.setSong(song);
            dataList.add(data);
        }
        return dataList;
    }

    public List<Singer> buildSimSingers(Singer singer){
        List<Singer> simSingers = new ArrayList<>();
        if (singer == null) {
            logger.info("singer为空");
            return simSingers;
        }
        List<String> simSingersId = singer.getSimilarSingerIds();
        for(String singerId : simSingersId){
            Singer simsinger = singerService.get(singerId);
            simSingers.add(simsinger);

        }
        return simSingers;

    }
}
