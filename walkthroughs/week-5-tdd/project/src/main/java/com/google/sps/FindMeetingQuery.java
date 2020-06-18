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

//changed from public final class to private static
public final class FindMeetingQuery {

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

    // filter out events that don't involve required attendees
    List<Event> filteredEvents = filterEvents(events, request);

    if (filteredEvents.isEmpty()){
      eventsAsTimeRange.add(TimeRange.fromStartDuration(0, 1440));
      return eventsAsTimeRange;
    }
    
    // sort events
    Collections.sort(filteredEvents, Event.ORDER_BY_START);
   
    // flatten events to TimeRanges representing when any required person is unavailable
    List<TimeRange> unavailableTimeRanges = getUnavailableTimeRanges(filteredEvents);

    // use unavailableTimeRanges to find available time ranges
    List<TimeRange> availableTimeRanges = getAvailableTimeRanges(unavailableTimeRanges);

    // check if there's enough time to hold meeting
    List<TimeRange> sufficientTimeRanges = getSufficientTimeRanges(availableTimeRanges, request);

    return sufficientTimeRanges;
  }

  public List<Event> filterEvents(Collection<Event> events, MeetingRequest request) {
    List<Event> filteredEvents = new ArrayList<Event>();
    Collection<String> requiredAttendees = request.getAttendees();

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

  public List<TimeRange> getUnavailableTimeRanges(List<Event> events) {
    List<TimeRange> unavailableTimeRanges = new ArrayList<TimeRange>();
    List<TimeRange> eventsAsTimeRanges = new ArrayList<TimeRange>();

    for (Event event: events) {
      eventsAsTimeRanges.add(event.getWhen());
    }
  
    // if there's just one unavailable slot, it cannot conflict with anything else
    if (eventsAsTimeRanges.size() <=1) {
      unavailableTimeRanges.add(eventsAsTimeRanges.get(0));
      return unavailableTimeRanges;
    }

    // time range objects are ordered, so you can compare them by order
    for (int i=0; i<=eventsAsTimeRanges.size()-2; i++) {
      TimeRange earlierTimeRange = eventsAsTimeRanges.get(i);
      TimeRange laterTimeRange = eventsAsTimeRanges.get(i+1);

      if (earlierTimeRange.overlaps(laterTimeRange)) { 
        int laterTRStart = laterTimeRange.start();
        int laterTREnd = laterTimeRange.end();

        // case 1: "regular" overlapping events 
        if (earlierTimeRange.contains(laterTRStart) && (! earlierTimeRange.contains(laterTREnd))) {
          TimeRange newTimeRange = TimeRange.fromStartEnd(earlierTimeRange.start(), laterTREnd, false);
          unavailableTimeRanges.add(newTimeRange);
        } else if (earlierTimeRange.contains(laterTimeRange)) {
          // case 2: nested events
          unavailableTimeRanges.add(earlierTimeRange);
        }
      } else {
        // case 3: non-overlapping events
        unavailableTimeRanges.add(earlierTimeRange);
        // be sure to account for last time range object
        if (i==eventsAsTimeRanges.size()-2) {
          unavailableTimeRanges.add(laterTimeRange);
        }
      }
    }
    return unavailableTimeRanges;
  }

  public List<TimeRange> getAvailableTimeRanges(List<TimeRange> unavailableTimeRanges) {
    List<TimeRange> availableTimeRanges = new ArrayList<TimeRange>();

    int earliestStartTime = 0;
    int latestEndTime = 1440;

    for (TimeRange timeRange: unavailableTimeRanges) {
      int unavailableStartTime = timeRange.start();
      int unavailableEndTime = timeRange.end();
    
      // split day up into two options, before and after the event.
      // if there is an existing "after" section from a previous event, break it up
      if (unavailableStartTime > earliestStartTime) {
        // this "before" section also serves as the previous day's "after" section
        TimeRange beforeAvailableTimeRange = TimeRange.fromStartEnd(earliestStartTime, unavailableStartTime, false);
        availableTimeRanges.add(beforeAvailableTimeRange);
      }
      // breaking up the previous "after" section
      if (availableTimeRanges.size() >= 2) {
        TimeRange lastAvailableTimeRange = availableTimeRanges.get(availableTimeRanges.size()-2);
        if (lastAvailableTimeRange.contains(unavailableStartTime)) {
          availableTimeRanges.remove(lastAvailableTimeRange); 
        }
      }
      // make a new "after" section
      if (unavailableEndTime < latestEndTime) {
        TimeRange afterAvailableTimeRange = TimeRange.fromStartEnd(unavailableEndTime, latestEndTime, false);
        availableTimeRanges.add(afterAvailableTimeRange);
      }
      //adjust start time "pointer"
      earliestStartTime = unavailableEndTime;
    }
    return availableTimeRanges;
  }

  public List<TimeRange> getSufficientTimeRanges(List<TimeRange> availableTimeRanges, MeetingRequest request) {
    List<TimeRange> sufficientTimeRanges = new ArrayList<TimeRange>();

    for (TimeRange timeRange: availableTimeRanges) {
      if ( (long)timeRange.duration() >= request.getDuration()) {
        sufficientTimeRanges.add(timeRange);
      }
    }
    return sufficientTimeRanges;
  }
}
