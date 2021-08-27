///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS mavencentral,jitpack
//DEPS io.quarkus:quarkus-bom:${quarkus.version:2.2.0.CR1}@pom
//DEPS io.quarkus:quarkus-qute
//DEPS https://github.com/w3stling/rssreader/tree/v2.5.0
//DEPS io.quarkus:quarkus-rest-client-reactive
//DEPS io.quarkus:quarkus-rest-client-reactive-jackson

//JAVA 16+
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import io.quarkus.qute.Engine;
import io.quarkus.qute.EvalContext;
import io.quarkus.qute.ReflectionValueResolver;
import io.quarkus.qute.ValueResolver;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.con text.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
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
                Max works as Distinguished Engineer at Red Hat, currently as part of the Quarkus team focusing on Developer joy.\s
                        
                Developer joy plays a central part of Max’s 15+ years of experience as a professional open-source contributor. Max worked on Hibernate/Hibernate Tools, WildFly, Seam, and Ceylon. Max led the team behind JBoss Tools and Developer Studio until starting work on Quarkus.
                        
                Quarkus being a Kubernetes native stack keeps Max busy ensuring developers still experience joy deploying Quarkus applications to Kubernetes platforms like OpenShift.
                        
                Max has a keen interest in moving the Java ecosystem forward and making it more accessible.
                To that end he created https://jbang.dev[JBang] a tool to bring back developer joy to Java and works closely with teams defining and exploring making native image for Java a reality using GraalVM/Mandrel, Leyden and Quarkus.
                        
                Max also co-hosts the weekly video podcast called https://quarkus.io/insights[Quarkus Insights] and he can be found on twitter as https://twitter.com/@maxandersen[@maxandersen]
                """;


        Collection<Item> sorted = new PriorityQueue<>(Collections.reverseOrder());
        RssReader reader = new RssReader();
        Stream<Item> rssFeed = reader.read("https://xam.dk/blog/feed.atom");
        sorted.addAll(rssFeed.limit(3).collect(Collectors.toList()));

        sorted.addAll(reader.read("https://quarkus.io/feed.xml").filter(p->p.getAuthor().get().contains("/maxandersen")).limit(3).collect(Collectors.toList()));

        Files.writeString(Path.of("readme.adoc"), qute.parse(Files.readString(Path.of("template.adoc.qute")))
                .data("bio", bio)
                .data("posts", sorted)
               // .data("video", video)
                .render());

        return 0;
    }

 static public class Items{

 }
     @RegisterRestClient(baseUri = "https://www.googleapis.com")
    public static interface PlaylistService {

        @GET
        @javax.ws.rs.Path("/youtube/v3/playlistItems")
        List<Items> playlist(@QueryParam("part") String part,
                          @QueryParam("maxResults") int maxResults,
                          @QueryParam("playlistId") String playListId,
                          @QueryParam("key") String key);
    }

    }

