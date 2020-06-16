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

    //if there are no events, return the entire day
    if (events.isEmpty()){
      eventsAsTimeRange.add(TimeRange.fromStartDuration(0, 1440));
      return eventsAsTimeRange;
    }

    //make list of event objects in order to get attendees later
    List<Event> eventsAsList = getSortedTimeRanges(events);
    
    throw new UnsupportedOperationException("TODO: represent events when any" +
        "required person is unavailable. Then get TimeRanges when everyone is available");
  }

  public List<Event> getSortedTimeRanges(Collection<Event> events) {
    // Collections.sort() only takes in List, so must convert events to a list
    List<Event> eventsAsList= new ArrayList<Event>();
    for (Event event: events) {
      eventsAsList.add(event);
    }

    Collections.sort(eventsAsList, Event.ORDER_BY_START);

    //logging eventsAsList to check ordering
    for (Event event: eventsAsList) {
      String eventTimeAsString = event.getWhen().toString();
      log.info(eventTimeAsString);
    }

    return eventsAsList;
  }
}
