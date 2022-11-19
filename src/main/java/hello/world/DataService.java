package hello.world;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSetMultimapCache;
import org.redisson.api.RedissonClient;
import org.redisson.api.LocalCachedMapOptions.CacheProvider;
import org.redisson.api.LocalCachedMapOptions.EvictionPolicy;
import org.redisson.api.LocalCachedMapOptions.ReconnectionStrategy;
import org.redisson.api.LocalCachedMapOptions.StoreMode;

import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Parallel;
import jakarta.inject.Inject;

@Context
@Parallel
public class DataService {

    @Inject
    private RedissonClient redisson;
    RScoredSortedSet<String> scoredSortedSet;
    LocalCachedMapOptions<String, String> localCachedMapOptions;
    RLocalCachedMap<String, String> localCachedMap;

    public DataService(RedissonClient redisson) {
        this.redisson = redisson;
        this.scoredSortedSet = redisson.getScoredSortedSet("hello");
        this.localCachedMapOptions = LocalCachedMapOptions.<String,String>defaults()
        .cacheSize(1000)
        // .cacheProvider(CacheProvider.CAFFEINE)
        .storeMode(StoreMode.LOCALCACHE_REDIS)
        .reconnectionStrategy(ReconnectionStrategy.LOAD)
        .evictionPolicy(EvictionPolicy.LRU)
        .timeToLive(10000, TimeUnit.SECONDS);

        this.localCachedMap = redisson.getLocalCachedMap("testMap",this.localCachedMapOptions);
    }

    public String getAll(){

        scoredSortedSet.add(10, "hello");
        scoredSortedSet.add(9, "world9");
        scoredSortedSet.add(8, "world8");
        scoredSortedSet.add(7, "world7");
        scoredSortedSet.add(6, "world6");
        scoredSortedSet.add(5, "world5");
  
        // Collection<String> readAll = scoredSortedSet.readAll();

        // for (String string : readAll) {
        //     // System.out.println(string);
        // }

        String str = "example";

        Collection<String> valueRange2 = scoredSortedSet.valueRange(0, 5);
        for (String string : valueRange2) {
            str += string;
            str += "  ";            
        }


        Collection<String> valueRange = scoredSortedSet.valueRangeReversed(0, 5);

        for (String string : valueRange) {
            str += string;
            str += "  ";  
        }

        scoredSortedSet.clear();

        str += Integer.toString(scoredSortedSet.size());

        return str;        
    }

    public String getAllMultimap(){

        RSetMultimapCache<String, String> setMultimapCache = redisson.getSetMultimapCache("myMultiMap");

        setMultimapCache.put("1", "a");
        setMultimapCache.put("1", "b");
        setMultimapCache.put("1", "c");
        setMultimapCache.put("1", "d");

        setMultimapCache.expireKey("1", 10, TimeUnit.SECONDS);

        Set<String> all = setMultimapCache.getAll("1");
        
        return all.toString();


    }

    public String getMultimap(){
        RSetMultimapCache<String, String> setMultimapCache = redisson.getSetMultimapCache("myMultiMap");

           
        Set<String> all = setMultimapCache.getAll("1");

        
        this.localCachedMap.put("hello", "World");
        
        return all.toString();
    }


}
