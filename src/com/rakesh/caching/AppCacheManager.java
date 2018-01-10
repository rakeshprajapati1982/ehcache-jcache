package com.rakesh.caching;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.util.Iterator;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class AppCacheManager {
	private static AppCacheManager instance = null;
	private CacheManager cacheManager = null;

	private AppCacheManager() {
		URI ehcacheCacheXmlUri = new File("./resources/ehcache.xml").toURI();
        CachingProvider cachingProvider = Caching.getCachingProvider();
        cacheManager = cachingProvider.getCacheManager(ehcacheCacheXmlUri, getClass().getClassLoader());
		System.out.println("Cache Initialized");
	}

	public static AppCacheManager getInstance() {
		if (instance == null) {
			instance = new AppCacheManager();
		}
		return instance;
	}
	
	public Cache<Integer, Person> getPersonCache(){
		return this.cacheManager.getCache("PersonCache", Integer.class, Person.class);
	}
	
    public String getStatistics(Cache<? extends Object, ? extends Object> cache) {
        try {
            StringBuffer b = new StringBuffer();
            ObjectName objectName = getJMXObjectName(cache);
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            
            // printing retrieved cache statistics to console.
            for (CacheStatistics cacheStatistic : CacheStatistics.values()) {
                b.append(cacheStatistic + "=" + mBeanServer.getAttribute(objectName, cacheStatistic.name()) + "\n");
            }
            return b.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private ObjectName getJMXObjectName(Cache<? extends Object, ? extends Object> cache){
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        
        // Refer to org.ehcache.jsr107.Eh107CacheStatisticsMXBean.Eh107CacheStatisticsMXBean(String, URI, StatisticsService)
        // and org.ehcache.jsr107.Eh107MXBean.Eh107MXBean(String, URI, String)
        final String beanName = "CacheStatistics";
        String cacheManagerName = sanitize(cache.getCacheManager().getURI().toString());
        String cacheName = sanitize(cache.getName());
        ObjectName objectName = null;
        try {
            objectName = new ObjectName(
                    "javax.cache:type=" + beanName + ",CacheManager=" + cacheManagerName + ",Cache=" + cacheName);
        }
        catch (MalformedObjectNameException e) {
            throw new CacheException(e);
        }
        
        if(!mBeanServer.isRegistered(objectName)){
           throw new CacheException("No MBean found with ObjectName => " + objectName.getCanonicalName());
        }
        
        return objectName;
    }
    
    private String sanitize(String string) {
        return ((string == null) ? "" : string.replaceAll(",|:|=|\n", "."));
    }
    
    public <K extends Object, V extends Object> long getSize(Cache<K, V> cache) {
        Iterator<Cache.Entry<K, V>> itr = cache.iterator();
        long count = 0;
        while(itr.hasNext()){
            itr.next();
            count++;
        }
        return count;
    }
    
    public <K extends Object, V extends Object> String dump(Cache<K, V> cache) {
        Iterator<Cache.Entry<K, V>> itr = cache.iterator();
        StringBuffer b = new StringBuffer();
        while(itr.hasNext()){
            Cache.Entry<K, V> entry = itr.next();
            b.append(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        return b.toString();
    }

    /**
     * Defining cache statistics parameters as constants.
     */
    private enum CacheStatistics {
        CacheHits, CacheHitPercentage,
        CacheMisses, CacheMissPercentage,
        CacheGets, CachePuts, CacheRemovals, CacheEvictions,
        AverageGetTime, AveragePutTime, AverageRemoveTime
    }   
	
}
