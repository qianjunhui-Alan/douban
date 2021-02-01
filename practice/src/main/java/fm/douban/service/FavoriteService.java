package fm.douban.service;

import fm.douban.model.Favorite;

import java.util.List;

public interface FavoriteService {
    public Favorite add(Favorite fav);
    public List<Favorite> list(Favorite favParam);
    public boolean delete(Favorite favParam);
}
