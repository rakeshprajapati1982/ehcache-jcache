package com.rakesh.caching;

import java.util.Date;

import org.ehcache.Cache;

public class MyMain {

	public static void main(String[] args) throws Exception {
		new MyMain().doMain();
	}
	
	private void doMain() throws Exception{
		Cache<Integer, Person> personCache = AppCacheManager.getInstance().getPersonCache();
		Person p;
		for(int i=1; i<=7; i++){
			p = new Person(i, "firstName-"+1, "lastName-"+i);
			personCache.put(p.getPersonId(), p);
		}
		
		System.out.println("Entries in the Cache at: " + new Date());
		System.out.println("--------------------");
		for(int i=1; i<=7; i++){
			p = new Person(i, "firstName-"+1, "lastName-"+i);
			System.out.println("Key: " + i + ", Value: " +personCache.get(i));
		}
		
		System.out.println("\nWaiting for 40 seconds\n");
		//Wait for 40 seconds so that we reach cache expiry threshold which is 30 seconds
		Thread.sleep(1000*40);
		System.out.println("\nAfter 40 seconds wait\n");
		
		System.out.println("Entries in the Cache at: " + new Date());
		System.out.println("--------------------");
		for(int i=1; i<=7; i++){
			p = new Person(i, "firstName-"+1, "lastName-"+i);
			System.out.println("Key: " + i + ", Value: " +personCache.get(i));
		}
	}

}
