package com.taiger.nlp.ner.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.taiger.nlp.ner.model.Location;



public interface LocationRepository extends PagingAndSortingRepository<Location, Integer> {
	
	List<Location> findTop3ByCountryNameIgnoreCaseContaining(String countryName);
	
	List<Location> findTop5ByCountryNameIgnoreCaseContaining(String countryName);
		
	List<Location> findTop3ByRegionNameIgnoreCaseContaining(String regionName);
	
	List<Location> findTop3ByCityNameIgnoreCaseContaining(String cityName);
	
	List<Location> findTop4ByCityNameIgnoreCaseContaining(String cityName);
	
	List<Location> findTop100ByCountryNameIgnoreCaseContainingOrCityNameIgnoreCaseContainingOrRegionNameIgnoreCaseContaining(String countryName, String cityName, String regionName);
	
	//List<Location> findREgistries();
	
}
