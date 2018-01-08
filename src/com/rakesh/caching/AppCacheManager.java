package com.rakesh.caching;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;

public class AppCacheManager {
	private static AppCacheManager instance = null;
	private CacheManager cacheManage = null;

	private AppCacheManager() {
		try {
			URL ehcacheXmlUrl = new File("./resources/ehcache.xml").toURI().toURL();
			Configuration xmlConfig = new XmlConfiguration(ehcacheXmlUrl);
			cacheManage = CacheManagerBuilder.newCacheManager(xmlConfig);
			cacheManage.init();
			System.out.println("Cache Initialized");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static AppCacheManager getInstance() {
		if (instance == null) {
			instance = new AppCacheManager();
		}
		return instance;
	}
	
	public Cache<Integer, Person> getPersonCache(){
		return this.cacheManage.getCache("PersonCache", Integer.class, Person.class);
	}
}
