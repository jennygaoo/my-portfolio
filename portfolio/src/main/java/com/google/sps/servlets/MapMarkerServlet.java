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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.MapMarker;
import com.google.gson.Gson;
import java.io.IOException;
import static java.lang.Double.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

@WebServlet("/mapmarkers")
public class MapMarkerServlet extends HttpServlet {
  private static final Gson gson = new Gson();
  //added _PARAM to variable names for clarity
  private static final String ITEM_NAME_PARAM = "itemName";
  private static final String CONTENT_PARAM = "content";
  private static final String LATITUDE_PARAM = "latitude";
  private static final String LONGITUDE_PARAM = "longitude";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    Collection<MapMarker> mapMarkers = getMapMarkers();
    String json = gson.toJson(mapMarkers);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    String itemName = Jsoup.clean(request.getParameter(ITEM_NAME_PARAM), Whitelist.none());
    double latitude = parseDouble(request.getParameter(LATITUDE_PARAM));
    double longitude = parseDouble(request.getParameter(LONGITUDE_PARAM));
    String content = Jsoup.clean(request.getParameter(CONTENT_PARAM), Whitelist.none());

    MapMarker mapMarker = new MapMarker(itemName, latitude, longitude, content);
    Entity mapMarkerEntity = new Entity("MapMarker");
    mapMarkerEntity.setProperty(ITEM_NAME_PARAM, mapMarker.getItemName());
    mapMarkerEntity.setProperty(LATITUDE_PARAM, mapMarker.getLatitude());
    mapMarkerEntity.setProperty(LONGITUDE_PARAM, mapMarker.getLongitude());
    mapMarkerEntity.setProperty(CONTENT_PARAM, mapMarker.getContent());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(mapMarkerEntity);
  }

  private Collection<MapMarker> getMapMarkers() {
    Collection<MapMarker> mapMarkers = new ArrayList<>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query mapMarkerQuery = new Query("MapMarker");
    PreparedQuery results = datastore.prepare(mapMarkerQuery);

    for (Entity mapMarkerEntity : results.asIterable()) {
      String itemName = (String) mapMarkerEntity.getProperty(ITEM_NAME_PARAM);
      double latitude = (double) mapMarkerEntity.getProperty(LATITUDE_PARAM);
      double longitude = (double) mapMarkerEntity.getProperty(LONGITUDE_PARAM);
      String content = (String) mapMarkerEntity.getProperty(CONTENT_PARAM);

      MapMarker mapMarker = new MapMarker(itemName, latitude, longitude, content);
      mapMarkers.add(mapMarker);
    }
    return mapMarkers;
  }
}
