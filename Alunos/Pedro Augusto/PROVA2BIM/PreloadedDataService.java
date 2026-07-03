import java.util.Arrays;
import java.util.List;

public class PreloadedDataService {

    public void populate(UserData userData) {
        List<Show> shows = buildShows();
        for (Show show : shows) {
            userData.addToWatchlist(show);
        }
    }

    private List<Show> buildShows() {
        return Arrays.asList(
            new Show(169, "Breaking Bad", "English",
                list("Drama", "Crime", "Thriller"),
                9.2, "Ended", "2008-01-20", "2013-09-29", "AMC",
                "A high school chemistry teacher diagnosed with inoperable lung cancer " +
                "turns to manufacturing and selling methamphetamine to secure his family's future.",
                null),

            new Show(82, "Game of Thrones", "English",
                list("Drama", "Adventure", "Fantasy"),
                9.3, "Ended", "2011-04-17", "2019-05-19", "HBO",
                "Seven noble families fight for control of the mythical land of Westeros. " +
                "Political and sexual intrigue abound.",
                null),

            new Show(2993, "Stranger Things", "English",
                list("Drama", "Horror", "Fantasy"),
                8.8, "Running", "2016-07-15", null, "Netflix",
                "When a young boy vanishes, a small town uncovers a mystery involving " +
                "secret experiments, terrifying supernatural forces and one strange little girl.",
                null),

            new Show(431, "Friends", "English",
                list("Comedy", "Romance"),
                8.8, "Ended", "1994-09-22", "2004-05-06", "NBC",
                "The misadventures of a group of friends living in Manhattan.",
                null),

            new Show(526, "The Office", "English",
                list("Comedy"),
                8.8, "Ended", "2005-03-24", "2013-05-16", "NBC",
                "A mockumentary about the everyday lives of office workers in the " +
                "Scranton, Pennsylvania branch of the fictional Dunder Mifflin Paper Company.",
                null),

            new Show(305, "Black Mirror", "English",
                list("Drama", "Thriller", "Science-Fiction"),
                8.5, "Running", "2011-12-04", null, "Netflix",
                "An anthology series exploring a twisted, high-tech near-future where " +
                "humanity's greatest innovations and darkest instincts collide.",
                null),

            new Show(1371, "Sherlock", "English",
                list("Drama", "Crime", "Mystery"),
                9.1, "Ended", "2010-07-25", "2017-01-15", "BBC One",
                "A modern update finds the famous consulting detective in 21st century London.",
                null),

            new Show(180, "Lost", "English",
                list("Adventure", "Drama", "Mystery"),
                8.4, "Ended", "2004-09-22", "2010-05-23", "ABC",
                "The survivors of a plane crash are forced to work together in order " +
                "to survive on a seemingly deserted tropical island.",
                null),

            new Show(1373, "The Wire", "English",
                list("Drama", "Crime", "Thriller"),
                9.2, "Ended", "2002-06-02", "2008-03-09", "HBO",
                "The Baltimore drug scene, as seen through the eyes of drug dealers " +
                "and law enforcement.",
                null)
        );
    }

    private List<String> list(String... items) {
        return Arrays.asList(items);
    }
}
