package fm.douban.spider;

import com.alibaba.fastjson.JSON;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//@Component
public class SingerSongSpider {
    private static Logger logger = LoggerFactory.getLogger(SingerSongSpider.class);

    private  static final String Cookie = "bid=tdprPACFYVQ; __utmv=30149280.22160; __gads=ID=dfefd17871c417a8:T=1597650157:S=ALNI_MaMxhr-A7y0_7nWfA92SbP8LLbxlg; gr_user_id=7aad110c-e14a-41a2-8f6f-a5889943a20b; _vwo_uuid_v2=D720C9E3AA5E4F9589344962FFCB667AC|85733030e9da62998c0f01ee204f1a9b; ll=\"118160\"; douban-profile-remind=1; __utma=30149280.1650612181.1597650126.1599354619.1601286840.18; __utmz=30149280.1601286840.18.12.utmcsr=accounts.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/; _ga=GA1.2.1650612181.1597650126; _pk_ref.100002.f71f=%5B%22%22%2C%22%22%2C1606990084%2C%22https%3A%2F%2Fham.youkeda.com%2F%22%5D; _pk_id.100002.f71f=4d9e833ad5488669.1606990084.1.1606990086.1606990084.; _gid=GA1.2.329834467.1607052097";

    private static final String SONG_URL = "https://fm.douban.com/j/v2/artist/{0}/";

    private static final String Song_Referer = "https://fm.douban.com/artist/{0}";

    private static final String HOST = "fm.douban.com";

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SongService songService;

    @Autowired
    private SubjectSpider subjectSpider;

    @PostConstruct
    public void init() {
        CompletableFuture.supplyAsync(() -> doExcute()).thenAccept(result ->logger.info("spider end ..."));
    }

    public boolean doExcute() {
        getSongDataBySingers();
        return true;
    }

    private void getSongDataBySingers() {
        List<Singer> singers = singerService.getAll();
        if (singers == null || singers.isEmpty()) {
            if(singers == null){
                logger.info("singers is null");
            }
            return;
        }

        // 遍历每个歌手
        singers.forEach(singer -> {
            String singerId = singer.getId();
            if(singerId == null){
                logger.info("singerId is null");
            }
            String url = MessageFormat.format(SONG_URL, singerId);
            String Referer = MessageFormat.format(Song_Referer, singerId);

            // 替换为自己使用浏览器开发者工具观察到的值
            Map<String, String> headerData = httpUtil.buildHeaderData(Referer, HOST, Cookie);
            String content = httpUtil.getContent(url, headerData);

            if (!StringUtils.hasText(content)) {
                return;
            }

            Map dataObj = null;

            try {
                dataObj = JSON.parseObject(content, Map.class);
            } catch (Exception e) {
                // 抛异常表示返回的内容不正确，不是正常的 json 格式，可能是网络或服务器出错了。
                logger.error("parse content to map error. ", e);
            }

            // 可能格式错误
            if (dataObj == null) {
                return;
            }

            // 解析关联的歌手
            Map relatedChannelData = (Map)dataObj.get("related_channel");
            // 保存关联的歌手后，收集关联歌手的 id
            List<String> similarIds = getRelatedSingers(relatedChannelData);
            // 设置给主歌曲
            singer.setSimilarSingerIds(similarIds);

            // 解析歌手的歌曲
            Map songlistData = (Map)dataObj.get("songlist");

            if (songlistData == null || songlistData.isEmpty()) {
                return;
            }

            List<Map> songsData = (List<Map>)songlistData.get("songs");

            if (songsData == null || songsData.isEmpty()) {
                return;
            }

            songsData.forEach(songObj ->{
                Song song = subjectSpider.buildSong(songObj);
                subjectSpider.saveSong(song);
            });

            // 保存主歌手数据，主要为了修改关联歌手 id 字段
            singerService.modify(singer);
        });
    }

    private List<String> getRelatedSingers(Map sourceData) {
        List<String> similarIds = new ArrayList<>();
        if (sourceData == null || sourceData.isEmpty()) {
            return similarIds;
        }

        List<Map> similarArtistsData = (List<Map>)sourceData.get("similar_artists");

        if (similarArtistsData == null || similarArtistsData.isEmpty()) {
            return similarIds;
        }

        similarArtistsData.forEach(sArtistObj ->{
            Singer singer = subjectSpider.buildSinger(sArtistObj);
            subjectSpider.saveSinger(singer);
            similarIds.add(singer.getId());
        });


        return similarIds;
    }

}
