/*
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.
*/

package logic.analysis;

import java.util.ArrayList;

import objs.Review;
import objs.stats.AvgDistrPriceStats;
import objs.stats.reports.ReportAvgDistrStats;
import storage.DBQuerying;

public class AvgDistrRateStatsAnalysis 
{
	public static int[] stars = new int[]{0, 1, 2, 3, 4, 5};

	
	public static ReportAvgDistrStats createReportAvgDistrRateStats()
	{//the average number of codes/review for each price range
		ReportAvgDistrStats rep = new ReportAvgDistrStats();
		
		for (int i = 0; i < stars.length - 1; i++)
		{
			AvgDistrPriceStats avg = new AvgDistrPriceStats();
			avg.setMin(stars[i]);
			avg.setMax(stars[i + 1]);
			
			int totalrevs = 0;
			
			ArrayList<String> apps = DBQuerying.getAppsForRateRange(stars[i], stars[i + 1]);
			ArrayList<Review> revs = new ArrayList<Review>();
			ArrayList<Integer> totalCodes = new ArrayList<Integer>();
			
			
			for (String appid : apps)
			{//create revs - list of all reviews of the apps in the price range
				ArrayList<Review> revsapp = DBQuerying.getAllRevsForApp(appid);
				for (Review r : revsapp)
					revs.add(r);
			}
			
			
			for (String appid : apps)
			{
				int T = DBQuerying.getTotalRevsForApp(appid);
				for (int index = 0; index < T; index++)
				{
					int trel = countCodesPerReview(revs, appid, ""+index);
					totalCodes.add(Integer.valueOf(trel)); //total codes for review index of appid
					System.out.println("Appid:" + appid + " rev index: " + index + " total codes: " + trel);
				}
			}
			
			int sum = 0;
			for (int k = 0; k < totalCodes.size(); k++)
				sum += totalCodes.get(k);
			avg.setAvg((double)sum/totalCodes.size());
			
			rep.report.add(avg);
		}
		return rep;
	}
	
	public static int countCodesPerReview(ArrayList<Review> revs, String appid, String revid)
	{
		int total = 0;
		for (Review r : revs)
		{
			if ((r.getAppid().equals(appid)) && (r.getRevid().equals(revid)))
				total++;
		}
		return total;
	}
	
	public static void main(String[] args)
	{
		AvgDistrRateStatsAnalysis.createReportAvgDistrRateStats().print();
	}
}
