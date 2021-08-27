///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS mavencentral,jitpack
//DEPS io.quarkus:quarkus-bom:${quarkus.version:2.2.0.CR1}@pom
//DEPS io.quarkus:quarkus-qute
//DEPS https://github.com/w3stling/rssreader/tree/v2.5.0
//DEPS io.quarkus:quarkus-rest-client-reactive
//DEPS io.quarkus:quarkus-rest-client-reactive-jackson

//JAVA 16+

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import io.quarkus.qute.Engine;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@QuarkusMain
public class update implements QuarkusApplication {

    @Inject
    Engine qute;

   // @RestClient
   // PlaylistService playlistService;

    public int run(String... args) throws Exception {

        String bio = """
                Otavio works as a Sr. Software Engineer in the Red Hat Integration team. He is currently focusing his work on all things related to Apache Camel.

                He has been working with messaging, integration, cloud and testing for more than 15 years. He continues to be passionate about these topics.

                Posts about his experiences and troubles with computers, ocasionally appear in his blogs in https://orpiske.net[English] and https://angusyoung.org[Portuguese].

                He is excited about Open Source and likes to contribute all sorts of projects. He is a regular committer at the https://camel.apache.org[Apache Camel] project, and makes or has made sporadic contributions to Fedora, Gentoo, Eclipse Paho, Apache ActiveMQ and others.

                He can be found on twitter as https://twitter.com/otavio021[@otavio021], speaking in English and Portuguese :brazil: about technology, science and life.
                """;


        Collection<Item> sorted = new PriorityQueue<>(Collections.reverseOrder());
        RssReader reader = new RssReader();
        Stream<Item> rssFeed = reader.read("https://www.orpiske.net/feed");
        sorted.addAll(rssFeed.limit(3).collect(Collectors.toList()));

        Files.writeString(Path.of("readme.adoc"), qute.parse(Files.readString(Path.of("template.adoc.qute")))
                .data("bio", bio)
                .data("posts", sorted)
                .render());

        return 0;
    }
}

