package com.identv.voxxy.twitter;


import com.identv.voxxy.common.utils.PubNubUtil;
import com.identv.voxxy.dto.VoxxyResponse;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import twitter4j.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Praveen on 23-12-2015.
 */
public class TwitterOperations {
    final PubNubUtil pubNubUtil = new PubNubUtil("pubnub.properties");

    public Set<Status> searchTweets(String hashTag) {
        Twitter twitter = new TwitterFactory().getInstance();
        Set<Status> tweets = new HashSet<Status>();
        Query query = new Query(hashTag);
        int numberOfTweets = 1;
        long lastID = Long.MAX_VALUE;
        long sinceId = Long.MIN_VALUE;
        // ArrayList<Status> tweets = new ArrayList<Status>();
        while (tweets.size() < numberOfTweets) {
            if (numberOfTweets - tweets.size() > 100)
                query.setCount(100);
            else
                query.setCount(numberOfTweets - tweets.size());
            try {
                QueryResult result = twitter.search(query);
                tweets.addAll(result.getTweets());
                System.out.println("Gathered " + tweets.size() + " tweets" + "\n");
                for (Status t : tweets) {
                    if (t.getId() < lastID)
                        lastID = t.getId();
                    if (t.getId() > sinceId)
                        sinceId = t.getId();
                }
            } catch (TwitterException te) {
                System.out.println("Couldn't connect: " + te);
            }
            query.setMaxId(lastID - 1);
        }
        return tweets;
    }


    public VoxxyResponse filterTweets(String[] hashTag) {
        final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        VoxxyResponse voxxyResponse = new VoxxyResponse();
        final String channelName = String.valueOf(UUID.randomUUID().toString()).concat(String.valueOf(new Date()));
        System.out.println("PublishKey" + pubNubUtil.getProperty("publishkey"));
        System.out.println(pubNubUtil.getProperty("subscribekey"));
        System.out.println(pubNubUtil.getProperty("secretkey"));
        final Pubnub pubnub = new Pubnub(pubNubUtil.getProperty("publishkey"), pubNubUtil.getProperty("subscribekey"), pubNubUtil.getProperty("secretkey"));

        StatusListener listener = new StatusListener() {

            @Override
            public void onStatus(Status status) {
                String statusJson = TwitterObjectFactory.getRawJSON(status);
                org.json.JSONObject message = null;
                try {
                    message = new org.json.JSONObject(statusJson);
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("ID Number" + message);

                //Code to publish in pubnub
                Callback callback = new Callback() {
                    public void successCallback(String channel, Object response) {
                        System.out.println("Success Callback:" + response.toString());
                    }

                    public void errorCallback(String channel, PubnubError error) {
                        System.out.println("Error Callback:" + error.toString());
                    }
                };
                pubnub.publish("test1", message, callback);
                System.out.println("Added to test");
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {

            }

            @Override
            public void onStallWarning(StallWarning warning) {

            }

            @Override
            public void onException(Exception ex) {
                System.out.println("Exception:" + ex.getMessage());
                twitterStream.cleanUp();
                twitterStream.shutdown();

            }
        };
        FilterQuery fq = new FilterQuery();
        fq.track(hashTag);
        twitterStream.addListener(listener);
        twitterStream.filter(fq);
        voxxyResponse.setChannelName(channelName);
        System.out.println("Channel Name" + voxxyResponse.getChannelName());
        return voxxyResponse;
    }
}
