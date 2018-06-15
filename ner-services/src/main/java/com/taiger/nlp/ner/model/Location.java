package com.taiger.nlp.ner.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="ip2location_db11")
public class Location {
	
	@Id
	@Column(name="ip_from")
	private Long ipFrom;
	@Column(name="ip_to")
	private Long ipTo;
	@Column(name="country_code")
	private String countryCode;
	@Column(name="country_name")
	private String countryName;
	@Column(name="region_name")
	private String regionName;
	@Column(name="city_name")
	private String cityName;
	private Double latitude;
	private Double longitude;
	@Column(name="zip_code")
	private String zipCode;
	@Column(name="time_zone")
	private String timeZone;
	
	@Transient
	private String undeterminedName;
	
	public boolean equal (Location l) {
		return (this.countryName.equals(l.getCountryName()) &&
				this.regionName.equals(l.getRegionName()) &&
				this.cityName.equals(l.getCityName()));
	}
	
}
