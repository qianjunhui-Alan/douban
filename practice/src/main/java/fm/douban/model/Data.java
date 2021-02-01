package fm.douban.model;

import java.util.List;

public class Data {
    private Singer singers;

    private Song song;

    private Singer master;

    public Singer getMaster() {
        return master;
    }

    public void setMaster(Singer master) {
        this.master = master;
    }

    public Singer getSingers() {
        return singers;
    }

    public void setSingers(Singer singers) {
        this.singers = singers;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
