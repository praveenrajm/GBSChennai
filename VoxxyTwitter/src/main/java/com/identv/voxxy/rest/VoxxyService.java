package com.identv.voxxy.rest;

import com.google.gson.Gson;
import com.identv.voxxy.common.utils.constants.IHttpHeaderNames;
import com.identv.voxxy.common.utils.PubNubUtil;
import com.identv.voxxy.common.utils.RestAuthenticator;
import com.identv.voxxy.dto.UserLoginInfo;
import com.identv.voxxy.dto.VoxxyResponse;
import com.identv.voxxy.twitter.TwitterOperations;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.sun.jersey.api.core.InjectParam;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.security.auth.login.LoginException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Praveen on 25-12-2015.
 */

@Path(value = "/voxxyservice")
public class VoxxyService {
    private static final Logger log = LoggerFactory.getLogger(VoxxyService.class);


    @InjectParam
    RestAuthenticator restAuthenticator;
    TwitterOperations twitterOperations = new TwitterOperations();


    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpHeaders httpHeaders, String auth) {
        UserLoginInfo userLoginInfo;
        try {
            userLoginInfo = new ObjectMapper().readValue(auth, UserLoginInfo.class);
            List<String> serviceKeyList = httpHeaders.getRequestHeader(IHttpHeaderNames.SERVICE_KEY);
            String serviceKey = serviceKeyList.get(0);
            String authToken = restAuthenticator.login(serviceKey, userLoginInfo.getUsername(), userLoginInfo.getPassword());
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("auth_token", authToken);
            javax.json.JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        } catch (final LoginException ex) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "Problem matching service key, username and password");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
        } catch (IOException e) {
            e.printStackTrace();
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "Problem matching service key, username and password");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
        }
    }

    private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status) {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setMaxAge(-1);
        cc.setMustRevalidate(true);
        return Response.status(status).cacheControl(cc);
    }

    @Path(value = "/searchTweet")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String searchTweet(@QueryParam("hashTag") String hashTag) {
        Set<Status> tweets = new HashSet<Status>();
        Gson gson = new Gson();
        if (hashTag != null) {
            System.out.println("Searching for Keyword" + hashTag);
            tweets = twitterOperations.searchTweets(hashTag);
        }
        return gson.toJson(tweets);
    }


    @POST
    @Path(value = "/filterTweets")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterTweets(String hashTag) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> keyWordList = null;
        VoxxyResponse voxxyResponse = null;
        try {
            keyWordList = mapper.readValue(hashTag, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] searchTags = keyWordList.toArray(new String[keyWordList.size()]);
        Gson gson = new Gson();
        System.out.println("Searching for Keyword" + searchTags);
        voxxyResponse = twitterOperations.filterTweets(searchTags);
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", gson.toJson(voxxyResponse));
        JsonObject jsonObj = jsonObjBuilder.build();
        return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
    }


    @POST
    @Path("logout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpHeaders httpHeaders) {
        UserLoginInfo userLoginInfo;
        try {

            List<String> serviceKeyList = httpHeaders.getRequestHeader(IHttpHeaderNames.SERVICE_KEY);
            List<String> authTokenList = httpHeaders.getRequestHeader(IHttpHeaderNames.AUTH_TOKEN);
            String serviceKey = serviceKeyList.get(0);
            String authToken = authTokenList.get(0);
            restAuthenticator.logout(serviceKey, authToken);
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "Logged out successfully");
            javax.json.JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        } catch (GeneralSecurityException e) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "Problem matching service key, token");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
        }
    }


    @GET
    @Path(value = "/samplePublish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String filterTweets() {
        final PubNubUtil pubNubUtil = new PubNubUtil("pubnub.properties");
        final Pubnub pubnub = new Pubnub(pubNubUtil.getProperty("publishkey"), pubNubUtil.getProperty("subscribekey"), pubNubUtil.getProperty("secretkey"));
        //Code to publish in pubnub
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println("Success Callback:" + response.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                System.out.println("Error Callback:" + error.toString());
            }
        };
        for (int i = 0; i <= 2000; i++) {
            pubnub.publish("test1", "message", callback);
            System.out.println("Added to test");
        }
        return "Published";
    }
}





