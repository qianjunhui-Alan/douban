package fm.douban.service;

import fm.douban.model.Singer;

import java.util.List;

public interface SingerService {
  public Singer addSinger(Singer singer);
  public Singer get(String singerId);
  public List<Singer> getAll();
  public boolean modify(Singer singer);
  public  boolean delete(String singerId);
}
