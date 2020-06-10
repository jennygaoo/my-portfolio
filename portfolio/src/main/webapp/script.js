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

let editMarker;

function randomizeRecipe() {
  // there are 4 recipes total
  const imageIndex = Math.floor(Math.random() * 4) + 1;
  const imgPath = "/images/recipes/" + imageIndex + ".jpg";

  const imgElement = document.createElement("img");
  imgElement.src = imgPath;
  const imageContainer = document.getElementById("random-image-container");

  if (imageIndex === 1) {
    const str = "ba's best chocolate chip cookies";
    const result = str.link("https://www.bonappetit.com/recipe/bas-best-chocolate-chip-cookies");
    document.getElementById("recipe-link").innerHTML = result;
  } else if (imageIndex === 2) {
    const str = "queer eye's antoni's lemon squares";
    const result = str.link("https://beta.theloop.ca/food/recipes/salty-lemon-squares.html");
    document.getElementById("recipe-link").innerHTML = result;
  } else if (imageIndex === 3) {
    const str = "pasta al limone";
    const result = str.link("https://www.bonappetit.com/recipe/pasta-al-limone"); 
    document.getElementById("recipe-link").innerHTML = result;
  } else if (imageIndex === 4) {
    const str = "basque burnt cheesecake";
    const result = str.link("https://www.bonappetit.com/recipe/basque-burnt-cheesecake");
    document.getElementById("recipe-link").innerHTML = result;
  } 

  imageContainer.innerHTML = "";
  imageContainer.appendChild(imgElement);
}

async function getComments() {
  const response = await fetch("/data");
  const comments = await response.json();
  const commentsElement = document.getElementById("comments");
  comments.forEach((comment) => {
    commentsElement.appendChild(createCommentsElement(comment));
  });
}

function createCommentsElement(comment) {
  const commentElement = document.createElement("li");
  commentElement.className = "comment";

  const contentElement = document.createElement("span");
  contentElement.innerText = comment.content;

  const deleteButtonElement = document.createElement("button");
  deleteButtonElement.innerText = "Delete";
  deleteButtonElement.addEventListener("click", () => {
    deleteComment(comment);

    commentElement.remove();
  });

  commentElement.appendChild(contentElement);
  commentElement.appendChild(deleteButtonElement);
  return commentElement;
}

function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append("id", comment.id);
  fetch("/delete-comment", {method: "POST", body: params});
}

function loadMap() {
  const sanFrancisco = {lat: 37.7749, lng: -122.4194};
  cafeMap = new google.maps.Map(
    document.getElementById("map"),
    {center: sanFrancisco, zoom: 12});

  cafeMap.addListener("click", (event) => {
    createMapMarkerForEdit(event.latLng.lat(), event.latLng.lng());
  });

  fetchMapMarkers();
  loadMapItem("Philz Coffee", 37.793949, -122.398062, "I like the Iced Coffee Rose!");
  loadMapItem("Philz Coffee", 37.793949, -122.398062, "I like the Iced Coffee Rose!");
  loadMapItem("Saint Frank Coffee", 37.779511, -122.410411, 
              "The hot chocolate & cappuccino are superb");
  loadMapItem("Stonemill Matcha", 37.764730, -122.421731, "I like the yuzu meringue");
  loadMapItem("Four Barrel Coffee", 37.768055, -122.422117, "The lattes are spectacular!");
}

async function fetchMapMarkers() {
  const response = await fetch("/mapmarkers");
  const mapMarkers = await response.json();
  mapMarkers.forEach((mapMarker) => {
    loadMapItem(mapMarker.itemName, mapMarker.lat, mapMarker.lng, mapMarker.content)
  });
}

function loadMapItem(itemName, latitudeValue, longitudeValue, itemDescription) {
  const itemMarker = new google.maps.Marker({
    position: {lat:latitudeValue, lng: longitudeValue},
    map: cafeMap,
    title: itemName
  });

  const itemInfoWindow = new google.maps.InfoWindow({
    content: "<h1>"+itemName+"</h1>"+"<p>"+itemDescription+"</p>"
  });

  itemMarker.addListener("click", function(){
    itemInfoWindow.open(cafeMap, itemMarker);
  });
}

function postMapMarker(itemName, lat, lng, content) {
  const params = new URLSearchParams();
  params.append("itemName", itemName);
  params.append("lat", lat);
  params.append("lng", lng);
  params.append("content", content);

  fetch("/mapmarkers", {method:"POST", body:params});
}

function createMapMarkerForEdit(lat, lng){
  // remove editable marker if one is already shown
  if (editMarker) {
    editMarker.setMap(null);
  }

  editMarker = new google.maps.Marker({position: {lat: lat, lng: lng}, map: cafeMap});

  const infoWindow = new google.maps.InfoWindow({content: buildInfoWindowInput(lat, lng)});

  google.maps.event.addListener(infoWindow, "closeclick", () => {
      editMarker.setMap(null);
  });

  console.log(editMarker);
  infoWindow.open(cafeMap, editMarker);
}

function buildInfoWindowInput(lat, lng) {
  const titleBox = document.createElement("textarea");
  const descriptionBox = document.createElement("textarea");
  const submitButton = document.createElement("button");
  submitButton.appendChild(document.createTextNode("Submit"));

  submitButton.onclick = () => {
    postMapMarker(titleBox.value, lat, lng, descriptionBox.value);
    loadMapItem(titleBox.value, lat, lng, descriptionBox.value);
    editMarker.setMap(null);
  };

  const entryBox = document.createElement("div");
  entryBox.appendChild(titleBox);
  entryBox.appendChild(document.createElement("br"));
  entryBox.appendChild(descriptionBox);
  entryBox.appendChild(document.createElement("br"));
  entryBox.appendChild(submitButton);

  return entryBox;
}
