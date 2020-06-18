// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

//changed from public final class to private static
public final class FindMeetingQuery {
  //TODO: class name input for Logger.getLogger() instead of manually inputting it
  private static Logger log = Logger.getLogger("FindMeetingQuery");

  //will use these later for manual testing
  public static final int START_OF_DAY = TimeRange.getTimeInMinutes(0, 0);
  public static final int END_OF_DAY = TimeRange.getTimeInMinutes(23, 59);
  
  private static final String PERSON_A = "Person A";
  private static final String PERSON_B = "Person B";
  private static final String PERSON_C = "Person C";

  private static final int TIME_0800AM = TimeRange.getTimeInMinutes(8, 0);
  private static final int TIME_0830AM = TimeRange.getTimeInMinutes(8, 30);
  private static final int TIME_0900AM = TimeRange.getTimeInMinutes(9, 0);
  private static final int TIME_0930AM = TimeRange.getTimeInMinutes(9, 30);
  private static final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 0);
  private static final int TIME_1100AM = TimeRange.getTimeInMinutes(11, 00);

  private static final int DURATION_30_MINUTES = 30;
  private static final int DURATION_60_MINUTES = 60;
  private static final int DURATION_90_MINUTES = 90;
  private static final int DURATION_1_HOUR = 60;
  private static final int DURATION_2_HOUR = 120;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> eventsAsTimeRange = new ArrayList<TimeRange>();

    // if there are no events & the request less than an entire day, return the entire day 
    if (events.isEmpty()) {
      if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
        // return empty arraylist
         return eventsAsTimeRange;
      } else {
         eventsAsTimeRange.add(TimeRange.fromStartDuration(0, 1440));
         return eventsAsTimeRange;
      }
    }

    //filter out events that don't involve required attendees
    List<Event> filteredEvents = filterEvents(events, request);

    // make list of event objects in order to get attendees later
    List<Event> eventsAsSortedList = getSortedTimeRanges(filteredEvents);

    //flatten events to TimeRanges representing when any required person is unavailable
    List<TimeRange> unavailableTimeRanges = getUnavailableTimeRanges(eventsAsSortedList);

    //use unavailableTimeRanges to find available time ranges
    // List<TimeRange> availableTimeRanges = getAvailableTimeRanges(unavailableTimeRanges);

    throw new UnsupportedOperationException("TODO: continue building schedule");
  }

  public List<Event> filterEvents(Collection<Event> events, MeetingRequest request) {
    List<Event> filteredEvents = new ArrayList<Event>();
    Collection<String> requiredAttendees = request.getAttendees();

    //not the most efficient way, but it works
    for (Event event: events) {
      Collection<String> eventAttendees = event.getAttendees();
      for (String attendee: eventAttendees) {
        if (requiredAttendees.contains(attendee)) {
          filteredEvents.add(event);
        }
      }
      
    }
    return filteredEvents;
  }

  public List<Event> getSortedTimeRanges(List<Event> events) {
    // Collections.sort() only takes in List, so must convert events to a list
    List<Event> eventsAsList= new ArrayList<Event>();
    for (Event event: events) {
      eventsAsList.add(event);
    }
    Collections.sort(eventsAsList, Event.ORDER_BY_START);
    return eventsAsList;
  }

  public List<TimeRange> getUnavailableTimeRanges(List<Event> events) {
    List<TimeRange> unavailableTimeRanges = new ArrayList<TimeRange>();
    List<TimeRange> eventsAsTimeRanges = new ArrayList<TimeRange>();

    for (Event event: events) {
      eventsAsTimeRanges.add(event.getWhen());
    }

    //case 1: events that do not overlap
    for (TimeRange timeRange_1: eventsAsTimeRanges) {
      boolean timeRangesOverlap = false;
      for (TimeRange timeRange_2: eventsAsTimeRanges)  {
        if (!(timeRange_1.equals(timeRange_2))) {
          if ((timeRange_1.overlaps(timeRange_2))) {
            timeRangesOverlap = true;
          }
        }
      }
      if (timeRangesOverlap == false) {
         // ensure no double-counting timeRanges
        if (!(unavailableTimeRanges.contains(timeRange_1))){
          unavailableTimeRanges.add(timeRange_1);
        }
      }
    }

    //very brute-force way to cover overlapping time ranges
    for (TimeRange timeRange_1: eventsAsTimeRanges) {
      for (TimeRange timeRange_2: eventsAsTimeRanges)  {
        if (!(timeRange_1.equals(timeRange_2))) {
          if ((timeRange_1.overlaps(timeRange_2))) {

            // case 2: "regular" overlapping events
            int timeR2Start = timeRange_2.start();
            int timeR2End = timeRange_2.end();

            //TODO: clean up the if statement below. basically check if 
            //timeRange_1 starts before and ends during timeRange_2
            if((timeRange_1.contains(timeR2Start)) && (!(timeRange_1.contains(timeR2End)))) {

              TimeRange newTimeRange = TimeRange.fromStartEnd(timeRange_1.start(), timeR2End, false);
              unavailableTimeRanges.add(newTimeRange);
            }

            // case 3: nested events
            else if (timeRange_1.contains(timeRange_2)) {
                unavailableTimeRanges.add(timeRange_1);
              }
            }
          }
        }
      }
    return unavailableTimeRanges;
  }

  public List<TimeRange> getAvailableTimeRanges(List<TimeRange> unavailableTimeRanges) {
      //"inverts" unavailableTimeRanges
      List<TimeRange> availableTimeRanges = new ArrayList<TimeRange>();
      for(TimeRange timeRange: unavailableTimeRanges) {
        // availableStartTime = 
        // availableEndTime = 
        // availableTimeRanges.add();
      }
    throw new UnsupportedOperationException("TODO: continue building this function");
  }
}
