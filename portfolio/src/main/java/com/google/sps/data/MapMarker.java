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

package com.google.sps.data;

/** Represents a marker on the map. */
public class MapMarker {

  private final String itemName;
  private final double latitude;
  private final double longitude;
  private final String content;

  public MapMarker(String itemName, double latitude, double longitude, String content) {
    this.itemName = itemName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.content = content;
  }

  public String getItemName() {
    return itemName;
  }
  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public String getContent() {
    return content;
  }
}
