package com.tracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tracker.models.LocationStats;

@Service
public class CoronaVirusDataService {

	public static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<LocationStats> allStats = new ArrayList<>();
	private Long totalCases=0L;
	
	
	public Long getTotalCases() {
		return totalCases;
	}
	public List<LocationStats> getAllStats() {
		return allStats;
	}
	@PostConstruct
	@Scheduled(cron = "* * 1 * * * ")
	public void fetchVirusData() throws IOException, InterruptedException {
		 Long newTotalCases=0L;

		List<LocationStats> newStats = new ArrayList<>();// the reason we are creating new list is we dont want to clear the allStats list and show error while data is being calculated and set in the list 
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
			HttpResponse<String> httpResponse =  client.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.print(httpResponse.body());

			StringReader csvReader = new StringReader(httpResponse.body());
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
			for (CSVRecord record : records) {
				LocationStats stat = new LocationStats();
			    stat.setState(record.get("Province/State"));
			    stat.setCountry(record.get("Country/Region"));
			    stat.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
			    int latestCases = Integer.parseInt(record.get(record.size()-1));
			    int prevDayCases = Integer.parseInt(record.get(record.size()-2));
			    stat.setDiffFromPreDay(latestCases-prevDayCases);
			    newStats.add(stat);
			    newTotalCases+=Long.parseLong(record.get(record.size()-1));
			}
			this.totalCases=newTotalCases;
			this.allStats = newStats;
			
	}
}
