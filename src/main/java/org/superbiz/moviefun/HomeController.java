package org.superbiz.moviefun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    Logger logger= LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("movieplatform")
    PlatformTransactionManager movieTransactionManager;

    @Autowired
    @Qualifier("albumplatform")
    PlatformTransactionManager albumTransactionManager;

   /*

    @Autowired
    @Qualifier("movieplatform")
    public void setMovieTransactionManager(PlatformTransactionManager movieTransactionManager) {
        this.movieTransactionManager = movieTransactionManager;
    }

    @Autowired
    @Qualifier("albumplatform")
    public void setAlbumTransactionManager(PlatformTransactionManager albumTransactionManager) {
        this.albumTransactionManager = albumTransactionManager;
    }*/

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures/*,PlatformTransactionManager movieTransactionManager,PlatformTransactionManager albumTransactionManager*/) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
/*
        this.albumTransactionManager=albumTransactionManager;
        this.movieTransactionManager=movieTransactionManager;
*/
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        logger.info("within setup");
        createmovies();

        createalbum();

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }



    private void createalbum() {
        DefaultTransactionDefinition td = new DefaultTransactionDefinition();
        td.setName("create album");
        td.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);


        TransactionStatus transaction = albumTransactionManager.getTransaction(null);

        for (Album album : albumFixtures.load()) {
            albumsBean.addAlbum(album);
        }
        albumTransactionManager.commit(transaction);

    }

    private void createmovies() {
        DefaultTransactionDefinition td = new DefaultTransactionDefinition();
        td.setName("create movie");
        td.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus transaction = movieTransactionManager.getTransaction(null);
        for (Movie movie : movieFixtures.load()) {
            moviesBean.addMovie(movie);
        }
        movieTransactionManager.commit(transaction);
    }
}
